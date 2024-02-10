public class BinaryNode{
    private BinaryNode left;
    private BinaryNode right;
    private Comparable value;

    public BinaryNode(Comparable val){
        value=val;
        left=null;
        right=null;
    }

    public void setRight(BinaryNode r){
        right=r;
    }
    public void setLeft(BinaryNode l){
        left=l;
    }

    public BinaryNode left(){
        return left;
    }

    public BinaryNode right(){
        return right;
    }
    public Comparable value(){
        return value;
    }

    public void setValue(Comparable x){
        value=x;
    }



}