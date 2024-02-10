public class Main {

    public static void main(String[] args){

        AVLTree tree = new AVLTree();

        System.out.println("insert for add, delete for delete");
        //7, 44, 2, 8, 48, 39, 41, 31, 32, 43, 40, 19, 37
        tree.add(new AVLNode(10));
        tree.add(new AVLNode(72));
        tree.add(new AVLNode(97));
        tree.add(new AVLNode(78));
        tree.add(new AVLNode(48));
        tree.add(new AVLNode(28));
        tree.add(new AVLNode(87));
        tree.add(new AVLNode(1));
        tree.add(new AVLNode(2));
        tree.add(new AVLNode(80));
        tree.add(new AVLNode(61));
        tree.add(new AVLNode(82));
        tree.add(new AVLNode(6));
        tree.add(new AVLNode(18));
        tree.add(new AVLNode(56));




        System.out.println(tree.preOrder());
    }
}
