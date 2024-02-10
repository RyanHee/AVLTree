public class Main {

    public static void main(String[] args){

        AVLTree tree = new AVLTree();

        System.out.println("insert for add, delete for delete");

        //7, 44, 2, 8, 48, 39, 41, 31, 32, 43, 40, 19, 37
        Integer[]lst={7, 44, 2, 8, 48, 39, 41, 31, 32, 43, 40, 19, 37};

        for (Integer i:lst){
            tree.add(new AVLNode(i));
        }

        //tree.add(new AVLNode(1));
        //tree.add(new AVLNode(2));
        //tree.add(new AVLNode(13));
        System.out.println(tree.fullLevelOrder());
        tree.remove(8);
        System.out.println(tree.levelOrder());
        System.out.println(tree.fullLevelOrder());
    }
}
