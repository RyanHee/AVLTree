# AVL Tree

A generic Java AVL tree with a Swing visualizer and a standalone binary search
tree. The BST uses the corrected implementation from the Binary-Search-Tree
project, including Javadoc, typed list traversals, empty-tree handling, and an
integer diameter accumulator. Its only structural adaptations are the default
package used here and protected root access for the AVL subclass.

## Tree functionality

`AVLTree<T>` extends `BinarySearchTree<T>`. Values must implement `Comparable`.
Both trees accept values directly, for example `tree.add(42)`. Duplicate values
are ignored using `compareTo(...) == 0`; null insertion, search, and removal
throw `NullPointerException`.

The AVL tree maintains a height difference of at most one between each node's
left and right subtrees. It caches node heights and applies single or double
rotations after insertion and deletion, including deletion of the root. Mutation
and lookup take O(log n) time. The standalone BST does not balance itself and can
take O(n) time on a chain.

| Method | Result |
| --- | --- |
| `add(value)` | Inserts a distinct value. |
| `contains(value)` | Checks for a comparison-equivalent value. |
| `remove(value)` | Returns a detached node containing the removed value, or null if absent. |
| `preOrder()` | Root, left subtree, right subtree. |
| `inOrder()` | Values in ascending order. |
| `postOrder()` | Left subtree, right subtree, root. |
| `reverseOrder()` | Values in descending order. |
| `levelOrder()` | Values level by level, left to right. |
| `getSmallest()` / `getLargest()` | Minimum / maximum, or null if empty. |
| `getNumNodes()` / `getNumLeaves()` | Total nodes / nodes without children. |
| `getHeight()` / `getNumLevels()` | Height in edges / number of occupied levels. |
| `getWidth()` / `getWidthAtLevel(level)` | Maximum level width / width at a zero-based depth. |
| `getDiameter()` | Number of nodes on the longest path anywhere in the tree. |
| `isFull()` | Whether every node has zero or two children; distinct from AVL balance. |

All five traversals return fresh `ArrayList<T>` instances. `toString()` displays
the inorder list. Empty trees have height -1, zero nodes, leaves, levels, width,
and diameter, and are considered full. Negative or nonexistent levels have
width zero. Traversals and tree statistics take O(n) time. These trees are not
thread-safe; stored values must retain their ordering while in the tree.

```java
AVLTree<Integer> tree = new AVLTree<>();
tree.add(30);
tree.add(20);
tree.add(10); // Right rotation restores balance.
System.out.println(tree.levelOrder()); // [20, 10, 30]
tree.remove(20);
System.out.println(tree.inOrder());    // [10, 30]
```

## Build and run

Requires JDK 11 or newer, with no external dependencies. Run from the project root:

```sh
mkdir -p build
javac -Xlint:all -Werror -d build src/*.java src/tests/*.java
java -cp build Main
```

Launch the Swing display:

```sh
java -cp build AVLTreeDriver
```

Enter space-separated integers in the input field, then choose **Add**, **Remove**,
or **Find**. Enter also adds values. Invalid input leaves the tree unchanged.
Found and newly added values are highlighted in green. Each node shows its
balance factor, and the header reports node count, height, and leaf count.

The display calculates a compact layout from the actual tree: parents are centered
above their children, subtrees are spaced to avoid overlapping nodes, and edges
connect only existing nodes. There is no six-level display limit or image-resource
dependency. Node positions animate to the new balanced layout after an operation;
a batch of values animates to its final layout rather than stepping through each
intermediate rotation.

- Scroll to zoom around the pointer.
- Drag the canvas to pan.
- Choose **Fit tree** to fit the entire tree and resume automatic fitting.

Manual zooming or panning preserves your camera position through updates. Fit mode
also adapts to window resizing. Very large trees can be zoomed in for readable labels.
The old `forDraw()` method remains for compatibility but is no longer used by the GUI.

## Tests

```sh
java -cp build TestBST
java -cp build TestAVL
java -Djava.awt.headless=true -cp build TestTreeView
```

Tests generate random values and print their seeds. Pass a printed seed to
reproduce a run, for example `java -cp build TestAVL 12345`.

The BST suite retains the earlier regression coverage and compares 10,000
randomized operations with `TreeSet`. The AVL suite checks all four insertion
rotations, repeated root removal, duplicates, null handling, drawing slots,
10,000 mixed operations, and sorted insertion/deletion. After each mutation it
verifies ordering, balance at every node, and cached heights independently.

The view tests check non-overlapping layouts, centered parents, all nodes and edges,
controls, invalid input, zoom/pan events, and headless rendering. To save a preview:

```sh
java -Djava.awt.headless=true -cp build TestTreeView /tmp/avl-preview.png
```
