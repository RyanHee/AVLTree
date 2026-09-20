# AVL Tree

A generic Java AVL tree with a Swing visualizer and a standalone binary search
tree. The BST uses the corrected implementation from the Binary-Search-Tree
project, including Javadoc, typed list traversals, empty-tree handling, and an
integer diameter accumulator. Its only structural adaptations are the `main`
package used here and protected root access for the AVL subclass.

Sources live under `src/main` (package `main`) and `src/tests` (package
`tests`), mirroring the Binary-Search-Tree project's layout.

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

Requires JDK 11 or newer. The tree and GUI have no external dependencies; only
the `TestAVL`/`TestBST` unit tests need JUnit (see [Tests](#tests)). Run from
the project root:

```sh
mkdir -p build
javac -Xlint:all -Werror -d build src/main/*.java
java -cp build main.Main
```

`Main` is a small console example: it inserts values that trigger rotations,
prints traversals and stats after each step, removes a value, and looks up a
few values. Sample output:

```text
Adding 30, 20, 10 (10 triggers a right rotation):
Level order: [20, 10, 30]
Height:      1

Adding 40, 50, 60, 25, 5 (more rotations along the way):
Level order: [40, 20, 50, 10, 30, 60, 5, 25]
In order:    [5, 10, 20, 25, 30, 40, 50, 60]
Nodes:       8
Leaves:      3
Height:      3

Removing 20:
In order:    [5, 10, 25, 30, 40, 50, 60]
Height:      3

Contains 50? true
Contains 20? false
Smallest / largest: 5 / 60
```

Launch the Swing display:

```sh
java -cp build main.AVLTreeDriver
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

`TestAVL` and `TestBST` are JUnit 5 tests, split into named `@Test` methods
with fixed seeds for reproducibility. They run against the JUnit Platform
Console Launcher's single standalone jar, so no Maven or Gradle is needed.
Download it once into an untracked `lib/` folder:

```sh
mkdir -p lib
curl -L -o lib/junit-platform-console-standalone-1.11.3.jar \
    https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.11.3/junit-platform-console-standalone-1.11.3.jar
```

Then compile and run:

```sh
mkdir -p build
javac -Xlint:all -Werror -cp lib/junit-platform-console-standalone-1.11.3.jar -d build src/main/*.java src/tests/*.java
java -jar lib/junit-platform-console-standalone-1.11.3.jar execute -cp build \
    --select-class tests.TestAVL --select-class tests.TestBST --details=tree
```

The BST suite retains the earlier regression coverage and compares 10,000
randomized operations with `TreeSet`. The AVL suite checks all four insertion
rotations, repeated root removal, duplicates, null handling, drawing slots,
10,000 mixed operations, and sorted insertion/deletion. After each mutation it
verifies ordering, balance at every node, and cached heights independently.

`TestVisualAVLTree` and `TestTreeView` stay as plain, dependency-free
`main()`-based runners (no JUnit needed for these):

```sh
java -cp build tests.TestVisualAVLTree
java -Djava.awt.headless=true -cp build tests.TestTreeView
```

Both print a random seed; pass a printed seed as an argument to reproduce a run,
for example `java -cp build tests.TestVisualAVLTree 12345`.

`VisualAVLTree` is a separate AVL implementation used only by the graphics
explorer; it tracks which node values took part in a rotation so the display
can animate them (see below). Its suite checks all four rotation shapes and a
removal-triggered rotation against the exact expected rotated node set, then
fuzzes 5,000 mixed operations against `TreeSet` while checking AVL invariants,
matching the coverage style of the AVL suite.

The view tests check non-overlapping layouts, centered parents, all nodes and edges,
controls, invalid input, zoom/pan events, and headless rendering. To save a preview:

```sh
java -Djava.awt.headless=true -cp build tests.TestTreeView /tmp/avl-preview.png
```
