import java.util.Stack;

public class AVLTree extends BinarySearchTree{


    public AVLTree(){
        super();
    }

    public void add(AVLNode node){
        super.add(node);
        Stack<BinaryNode> stack = super.findAllAncestors(node.value());
        //System.out.println(stack);
        while (!stack.isEmpty()){
            AVLNode g=(AVLNode) stack.pop();
            if (g.getBalanceFactor()==2){
                AVLNode left = (AVLNode) g.left();
                if (left.getBalanceFactor()==1){
                    //left left rotation;
                    BinaryNode c = leftLeftRotation(g, left);
                    if (!stack.isEmpty()){
                        BinaryNode a = stack.pop();
                        if (a.value().compareTo(g.value())>0){
                            a.setLeft(c);
                        }
                        else{
                            a.setRight(c);
                        }
                    }
                    else{
                        super.setRoot(c);
                    }

                }
                else if (left.getBalanceFactor()==-1){
                    //left right rotation
                    BinaryNode c = leftRightRotation(g, left, left.right());
                    if (!stack.isEmpty()){
                        BinaryNode a = stack.pop();
                        if (a.value().compareTo(g.value())>0){
                            a.setLeft(c);
                        }
                        else{
                            a.setRight(c);
                        }
                    }
                    else{
                        super.setRoot(c);
                    }
                }
            }
            else if (g.getBalanceFactor()==-2){
                AVLNode right = (AVLNode) g.right();
                if (right.getBalanceFactor()==1){
                    //right left rotation
                    BinaryNode c = rightLeftRotation(g, right, right.left());
                    if (!stack.isEmpty()){
                        BinaryNode a = stack.pop();
                        if (a.value().compareTo(g.value())>0){
                            a.setLeft(c);
                        }
                        else{
                            a.setRight(c);
                        }
                    }
                    else{
                        super.setRoot(c);
                    }
                }
                else if (right.getBalanceFactor()==-1){
                    //right right rotation
                    BinaryNode c = rightRightRotation(g, right);
                    if (!stack.isEmpty()){
                        BinaryNode a = stack.pop();
                        if (a.value().compareTo(g.value())>0){
                            a.setLeft(c);
                        }
                        else{
                            a.setRight(c);
                        }
                    }
                    else{
                        super.setRoot(c);
                    }
                }
            }
        }

    }


    public BinaryNode remove(Comparable i){
        BinaryNode removenode = super.get(i);
        if (removenode==null){
            return null;
        }

        Stack<BinaryNode>stack=new Stack<>();
        BinaryNode retNode=null;
        if (removenode.left()!=null && removenode.right()!=null){
            BinaryNode successor = super.successor(removenode);
            BinaryNode succParent= super.parent(successor.value());
            System.out.println(succParent.value());
            retNode=super.remove(i);
            stack = super.findAllAncestors(succParent.value());
        }
        else{
            BinaryNode parent = super.parent(removenode.value());
            retNode=super.remove(i);
            stack=super.findAllAncestors(parent.value());
        }

        //BinaryNode successor = super.successor(removenode);




        System.out.println("Stack: "+stack);
        while (!stack.isEmpty()){
            AVLNode g=(AVLNode) stack.pop();
            System.out.println(g+" "+g.getBalanceFactor());
            if (g.getBalanceFactor()==2){
                AVLNode left = (AVLNode) g.left();
                if (left.getBalanceFactor()!=-1){
                    //left left rotation;
                    //System.out.println("g: "+g+" l: "+g.left()+" r: "+g.right());
                    BinaryNode c = leftLeftRotation(g, left);
                    if (!stack.isEmpty()){
                        BinaryNode a = stack.pop();
                        if (a.value().compareTo(g.value())>0){
                            a.setLeft(c);
                        }
                        else{
                            a.setRight(c);
                        }
                    }
                    else{
                        super.setRoot(c);
                    }

                }
                else{
                    //left right rotation
                    BinaryNode c = leftRightRotation(g, left, left.right());
                    if (!stack.isEmpty()){
                        BinaryNode a = stack.pop();
                        if (a.value().compareTo(g.value())>0){
                            a.setLeft(c);
                        }
                        else{
                            a.setRight(c);
                        }
                    }
                    else{
                        super.setRoot(c);
                    }
                }
            }
            else if (g.getBalanceFactor()==-2){
                AVLNode right = (AVLNode) g.right();
                if (right.getBalanceFactor()==1){
                    //right left rotation
                    BinaryNode c = rightLeftRotation(g, right, right.left());
                    if (!stack.isEmpty()){
                        BinaryNode a = stack.pop();
                        if (a.value().compareTo(g.value())>0){
                            a.setLeft(c);
                        }
                        else{
                            a.setRight(c);
                        }
                    }
                    else{
                        super.setRoot(c);
                    }
                }
                else{
                    //right right rotation
                    BinaryNode c = rightRightRotation(g, right);
                    if (!stack.isEmpty()){
                        BinaryNode a = stack.pop();
                        System.out.println("a: "+a);
                        if (a.value().compareTo(g.value())>0){
                            a.setLeft(c);
                        }
                        else{
                            a.setRight(c);
                        }
                    }
                    else{
                        super.setRoot(c);
                    }
                }
            }
        }


        return retNode;


    }

    private BinaryNode leftLeftRotation(BinaryNode g, BinaryNode p){
        g.setLeft(p.right());
        p.setRight(g);
        return p;
    }

    private BinaryNode leftRightRotation(BinaryNode g, BinaryNode p, BinaryNode c){
        g.setLeft(c);
        p.setRight(c.left());
        c.setLeft(p);
        return leftLeftRotation(g, c);
    }

    private BinaryNode rightRightRotation(BinaryNode g, BinaryNode p){
        g.setRight(p.left());
        p.setLeft(g);
        return p;
    }

    private BinaryNode rightLeftRotation(BinaryNode g, BinaryNode p, BinaryNode c){
        g.setRight(c);
        p.setLeft(c.right());
        c.setRight(p);
        return rightRightRotation(g, c);
    }
}
