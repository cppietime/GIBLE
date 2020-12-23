import com.funguscow.gible.util.CodePointStream;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class EncodingTests {

    public static void testFile(InputStream stream, Charset charset){
        try{
            List<Integer> accum = new ArrayList<>();
            char[] chars = new char[1024];
            BufferedInputStream bis = new BufferedInputStream(stream);
            bis.mark(1000);
            InputStreamReader reader;
            reader = new InputStreamReader(bis, charset);
            int i;
            while((i = reader.read()) != -1){
                accum.add(i);
            }
            bis.reset();
            bis.mark(1000);
            System.out.println("Reading " + charset + " w/ read -> " + accum);
            int read = reader.read(chars, 0, 1024);
            bis.reset();
            bis.mark(1000);
            System.out.print("Read " + read + " chars: ");
            for(i = 0; i < read; i++)
                System.out.print(Integer.toHexString(chars[i]) + ", ");
            System.out.println();
            reader.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void testFileForAll(File file) throws FileNotFoundException {
        System.out.println("\nFor file " + file.getName() + "=========\n");
        testFile(new FileInputStream(file), StandardCharsets.US_ASCII);
        testFile(new FileInputStream(file), StandardCharsets.UTF_8);
        testFile(new FileInputStream(file), StandardCharsets.UTF_16);
        testFile(new FileInputStream(file), StandardCharsets.UTF_16LE);
        testFile(new FileInputStream(file), StandardCharsets.UTF_16BE);
    }

    public static  void testString(String str) throws Exception{
        System.out.println("\n For string " + str + " ===============\n");
        System.out.print("bytes = ");
        for(byte b : str.getBytes())
            System.out.print(Integer.toHexString(b & 0xff) + ", ");
        System.out.println();
        testFile(new ByteArrayInputStream(str.getBytes(StandardCharsets.US_ASCII)), StandardCharsets.US_ASCII);
        testFile(new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
        testFile(new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_16)), StandardCharsets.UTF_16);
        testFile(new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_16LE)), StandardCharsets.UTF_16LE);
        testFile(new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_16BE)), StandardCharsets.UTF_16BE);
        testFile(new ByteArrayInputStream(str.getBytes(Charset.defaultCharset())), Charset.defaultCharset());
    }

    public static void main(String... args){
        try {
            String str = "St\u0100ring";
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream("utf8bom.txt"));
            CodePointStream stream = new CodePointStream(bis, StandardCharsets.UTF_8);
            int i;
            while((i = stream.read()) != -1)
                System.out.printf("Read 0x%06x\n", i);
            bis.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
