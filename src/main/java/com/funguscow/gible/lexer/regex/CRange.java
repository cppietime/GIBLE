package com.funguscow.gible.lexer.regex;

import com.funguscow.gible.util.CodePointHelper;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A range of code points
 */
public class CRange implements Comparable<CRange>, Cloneable{

    private int min, max;
    private Set<Integer> values;
    private boolean marks;

    /**
     * A range from min to max, matching values
     * @param min Low end (inclusive)
     * @param max High end (exclusive)
     * @param values Match values
     */
    public CRange(int min, int max, Integer... values){
        this(min, max, new HashSet<>(Arrays.asList(values)));
    }

    /**
     * A range from min to max, matching values
     * @param min Low end (inclusive)
     * @param max High end (exclusive)
     * @param values Match values
     */
    public CRange(int min, int max, Set<Integer> values){
        this.min = min;
        this.max = max;
        this.values = values;
    }

    /**
     * Turns this range into a placeholder for an integer code point
     * @param i Value of code point
     * @return this
     */
    public CRange makeInt(int i){
        min = max = i;
        return this;
    }

    /**
     * Add i to matching values
     * @param i Value to add
     * @return this
     */
    public CRange addValue(int i){
        values.add(i);
        return this;
    }

    /**
     * Mark whether this marks a lookahead point
     * @param m Should this lookahead
     * @return this
     */
    public CRange mark(boolean m){
        marks = m;
        return this;
    }

    /**
     *
     * @return True if this indicates a lookahead point
     */
    public boolean doesMark(){
        return marks;
    }

    /**
     *
     * @return Set of values this leads to
     */
    public Set<Integer> getValues(){
        return values;
    }

    /**
     *
     * @return Low end (inclusive)
     */
    public int getMin(){
        return min;
    }

    /**
     *
     * @return High end (exclusive)
     */
    public int getMax(){
        return max;
    }

    public boolean contains(int i){
        return (i >= min && i < max);
    }

    public CRange clone(){
        return new CRange(min, max);
    }

    public int hashCode(){
        return min * 101 + max * 17 + values.hashCode();
    }

    public int compareTo(CRange other){
        if(min != other.min)
            return min - other.min;
        if(max != other.max)
            return other.max - max;
        if(values.size() != other.values.size())
            return values.size() - other.values.size();
        Iterator<Integer> me = values.iterator(), him = other.values.iterator();
        while(me.hasNext()){
            int a = me.next();
            int b = him.next();
            if(a != b)
                return a - b;
        }
        return 0;
    }

    public boolean equals(Object obj){
        if(this == obj)
            return true;
        if(!(obj instanceof CRange))
            return false;
        CRange other = (CRange)obj;
        return min == other.min && max == other.max && values.equals(other.values);
    }

    public String toString(){
        return (marks ? "@" : "") + String.format("[%d-%d(%s)]", min, max, Arrays.toString(values.toArray()));
    }

    /**
     * Combine overlapping ranges
     * @param collections Set of ranges to join
     * @return Union of ranges
     */
    public static TreeSet<CRange> union(Collection<CRange>... collections){
        List<CRange> ranges = new ArrayList<>();
        for(Collection<CRange> collection : collections)
            ranges.addAll(collection);
        Collections.sort(ranges);
        int openAt = -1;
        int closeAt = -1;
        TreeSet<CRange> union = new TreeSet<>();
        Set<Integer> values = new HashSet<>();
        for(CRange range : ranges){
            if(openAt == -1){
                openAt = range.min;
                closeAt = range.max;
            }
            else{
                if(range.min <= closeAt) {
                    closeAt = Math.max(closeAt, range.max);
                }
                else{
                    CRange cr = new CRange(openAt, closeAt, values);
                    values = new HashSet<>();
                    if(cr.max > cr.min)
                        union.add(cr);
                    openAt = range.min;
                    closeAt = range.max;
                }
            }
            values.addAll(range.values);
        }
        if(openAt != -1) {
            CRange cr = new CRange(openAt, closeAt);
            cr.values = values;
            if(cr.max > cr.min)
                union.add(cr);
        }
        return union;
    }

    public static TreeSet<CRange> intersection(Collection<CRange> left, Collection<CRange> right){
        // union is called to merge overlapping ranges
        List<CRange> leftList = new ArrayList<>((left)), rightList = new ArrayList<>((right));
        Collections.sort(leftList);
        Collections.sort(rightList);
        int leftPtr = 0, rightPtr = 0;
        TreeSet<CRange> intersection = new TreeSet<>();
        while(leftPtr < leftList.size() && rightPtr < rightList.size()){
            CRange l = leftList.get(leftPtr), r = rightList.get(rightPtr);
            if(l.max < r.min){
                leftPtr ++;
            }
            else if(r.max < l.min){
                rightPtr ++;
            }
            else{
                CRange cr = new CRange(Math.max(l.min, r.min), Math.min(l.max, r.max));
                cr.values = new HashSet<>();
                cr.values.addAll(l.values);
                cr.values.addAll(r.values);
                if(cr.max > cr.min)
                    intersection.add(cr);
                if(l.max == r.max){
                    leftPtr ++;
                    rightPtr ++;
                }
                else if(l.max > r.max)
                    rightPtr ++;
                else
                    leftPtr ++;
            }
        }
        return intersection;
    }

    private static class Boundary implements Comparable<Boundary>{
        int position;
        boolean open, marking;
        Set<Integer> values;
        public Boundary(int p, boolean o, boolean m, Set<Integer> v){
            position = p;
            open = o;
            marking = m;
            values = new HashSet<>(v);
        }
        public int compareTo(Boundary other){
            int pdif = position - other.position;
            if(pdif != 0)
                return pdif;
            if(open && !other.open)
                return 1;
            if(!open && other.open)
                return -1;
            if(marking && !other.marking)
                return 1;
            if(!marking && other.marking)
                return -1;
            if(values.size() != other.values.size())
                return values.size() - other.values.size();
            Iterator<Integer> me = values.iterator(), him = other.values.iterator();
            while(me.hasNext()){
                int a = me.next(), b = him.next();
                if(a != b)
                    return a - b;
            }
            return 0;
        }

        public String toString(){
            return String.format("%d%s", position, Arrays.toString(values.toArray()));
        }
    }

    /**
     * Separate overlapping ranges and their values
     * @param rangeGroups Any number of range collections
     * @return A new collection without overlaps
     */
    public static TreeSet<CRange> breakApart(Collection<CRange>... rangeGroups){
        List<Boundary> boundaries = new ArrayList<>();
        for(Collection<CRange> ranges : rangeGroups){
            for(CRange range : ranges){
                boundaries.add(new Boundary(range.min, true, range.doesMark(), range.values));
                boundaries.add(new Boundary(range.max, false, range.doesMark(), range.values));
            }
        }
        Boundary terminal = (new Boundary(CodePointHelper.MAX_CODE_POINT, false, false, boundaries.stream()
                .map(b -> b.values)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet())));
        boundaries.add(terminal);
        Collections.sort(boundaries);
        Map<Integer, Integer> mults = new HashMap<>();
        TreeSet<CRange> result = new TreeSet<>();
        int marker = 0;
        int marking = 0;
        for(Boundary boundary : boundaries){
            Set<Integer> beginning = mults.entrySet().stream()
                    .filter(entry -> entry.getValue() > 0)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet());
            int mark0 =  marking;
            if(boundary.open){
                boundary.values.forEach(i -> {
                    int j = mults.computeIfAbsent(i, k -> 0);
                    mults.put(i, j + 1);
                });
                if(boundary.marking)
                    marking++;
            }
            else{
                boundary.values.forEach(i -> {
                    int j = mults.computeIfAbsent(i, k -> 0);
                    mults.put(i, j - 1);
                });
                if(boundary.marking)
                    marking--;
            }
            Set<Integer> end = mults.entrySet().stream()
                    .filter(entry -> entry.getValue() > 0)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet());
            if(!end.equals(beginning) || (marking > 0 && mark0 <= 0) || (marking <= 0 && mark0 > 0)){
                if(!beginning.isEmpty() && boundary.position > marker){
                    CRange range = new CRange(marker, boundary.position, beginning).mark(mark0 > 0);
                    result.add(range);
                }
                marker = boundary.position;
            }
        }
        return result;
    }

    /**
     * Get ranges excluding the provided
     * @param minuend Complement of result
     * @return Complement of minuend
     */
    public static TreeSet<CRange> complement(Collection<CRange> minuend){
        int marker = 0;
        TreeSet<CRange> sorted = new TreeSet<>(minuend);
        TreeSet<CRange> complement = new TreeSet<>();
        for(CRange range : sorted){
            int end = range.getMin();
            if(end > marker)
                complement.add(new CRange(marker, end));
            marker = range.getMax();
        }
        if(marker < CodePointHelper.MAX_CODE_POINT)
            complement.add(new CRange(marker, CodePointHelper.MAX_CODE_POINT));
        return complement;
    }

}
