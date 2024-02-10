public class AVLNode extends BinaryNode{

    public AVLNode(Comparable c){
        super(c);
    }

    public int getBalanceFactor(){
        return getHeight(super.left())-getHeight(super.right());
    }

    private int getHeight(BinaryNode k)
    {
        if(k == null)
            return -1;
        return 1 + Math.max(getHeight(k.left()),
                getHeight(k.right()));
    }

    public String toString(){
        return value().toString();
    }
}
