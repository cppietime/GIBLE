import com.funguscow.gible.lexer.regex.CRange;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

public class CRangeTest {

    public static void main(String[] args){
        Set<CRange> ranges = Set.of(
                new CRange(0, 20, 4, 5),
                new CRange(30, 50, 1, 2),
                new CRange(110, 150, 0)
        );
        System.out.println(Arrays.toString(CRange.union(ranges).toArray()));
        Set<CRange> second = Set.of(
                new CRange(200, 210, 0),
                new CRange(300, 310)
        );
        System.out.println(Arrays.toString(CRange.union(ranges, second).toArray()));
        System.out.println(Arrays.toString(CRange.intersection(ranges, second).toArray()));
        System.out.println(Arrays.toString(CRange.breakApart(ranges, second).toArray()));
        second = Set.of(
                new CRange(180, 198).mark(true),
                new CRange(200, 500, 3, 4),
                new CRange(400, 505, 4, 5)
        );
        System.out.println(Arrays.toString(CRange.union(ranges, second).toArray()));
        System.out.println(Arrays.toString(CRange.intersection(ranges, second).toArray()));
        System.out.println(Arrays.toString(CRange.breakApart(ranges, second).toArray()));
        ranges = Set.of(
                new CRange(12, 66, 0).mark(true),
                new CRange(40, 81, -5),
                new CRange(90, 120),
                new CRange(160, 204, 1).mark(true),
                new CRange(420, 450, 11),
                new CRange(500, 520, 100)
        );
        System.out.println(Arrays.toString(CRange.union(ranges, second).toArray()));
        System.out.println(Arrays.toString(CRange.intersection(ranges, second).toArray()));
        TreeSet<CRange> broken = CRange.breakApart(ranges, second);
        System.out.println(Arrays.toString(broken.toArray()));
        CRange place = new CRange(70, 70);
        System.out.println(place);
        System.out.println(broken.floor(place));
        System.out.println(broken.ceiling(place));
        System.out.println(broken.lower(place));
        System.out.println(broken.higher(place));
        place = new CRange(66, 66);
        System.out.println(place);
        System.out.println(broken.floor(place));
        System.out.println(broken.ceiling(place));
        System.out.println(broken.lower(place));
        System.out.println(broken.higher(place));
        place = new CRange(0, 0);
        System.out.println(place);
        System.out.println(broken.floor(place));
        System.out.println(broken.ceiling(place));
        System.out.println(broken.lower(place));
        System.out.println(broken.higher(place));
    }

}
