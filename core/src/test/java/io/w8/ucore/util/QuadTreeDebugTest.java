package io.w8.ucore.util;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuadTreeDebugTest {

    @Test
    void shouldSplitAndDumpDebugInfo() {
        QuadTree<TestObject> tree = new QuadTree<>(1, new Rectangle(0f, 0f, 100f, 100f));

        TestObject leftBottom = new TestObject(5f, 5f, 10f, 10f);
        TestObject rightBottom = new TestObject(60f, 5f, 10f, 10f);
        TestObject center = new TestObject(45f, 45f, 20f, 20f);

        tree.insert(leftBottom);
        tree.insert(rightBottom);
        tree.insert(center);

        String debug = tree.debugDump();
        System.out.println("\n[QuadTree debug dump]\n" + debug);

        assertFalse(tree.isLeaf(), "Tree must split when max objects per node is exceeded.");
        assertTrue(debug.contains("node(level=0"));
        assertTrue(debug.contains("leaf=false"));
        assertTrue(debug.contains("objects=1"), "Center object should stay in root node.");

        Array<TestObject> nearby = new Array<>();
        tree.getIntersect(nearby, new Rectangle(0f, 0f, 30f, 30f));
        assertTrue(nearby.contains(leftBottom, true));
        assertTrue(nearby.contains(center, true));
    }

    private static final class TestObject implements QuadTree.QuadTreeObject {
        private final Rectangle bounds;

        private TestObject(float x, float y, float w, float h) {
            this.bounds = new Rectangle(x, y, w, h);
        }

        @Override
        public void getBoundingBox(Rectangle out) {
            out.set(bounds);
        }
    }
}
