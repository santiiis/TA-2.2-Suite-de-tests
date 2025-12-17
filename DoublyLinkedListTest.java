import java.lang.reflect.Field;

public class DoublyLinkedListTest {

    private static void ok(boolean cond, String msg) {
        if (!cond) throw new AssertionError("FALLÓ: " + msg);
    }

    private static void eq(String exp, String act, String msg) {
        if (!exp.equals(act)) {
            throw new AssertionError("FALLÓ: " + msg + " | esperado=" + exp + " obtenido=" + act);
        }
    }

    private static DDL_Node head(DoublyLinkedList list) {
        return (DDL_Node) getPrivateField(list, "head");
    }

    private static DDL_Node tail(DoublyLinkedList list) {
        return (DDL_Node) getPrivateField(list, "tail");
    }

    private static Object getPrivateField(Object obj, String fieldName) {
        try {
            Field f = obj.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            return f.get(obj);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo leer el campo privado '" + fieldName + "': " + e.getMessage());
        }
    }

    private static String view(DoublyLinkedList list) {
        StringBuilder sb = new StringBuilder();
        DDL_Node cur = head(list);
        while (cur != null) {
            sb.append(cur.data);
            if (cur.next != null) sb.append(" ");
            cur = cur.next;
        }
        return sb.toString();
    }

    private static DoublyLinkedList listOf(int... values) {
        DoublyLinkedList list = new DoublyLinkedList();
        for (int v : values) list.insertAtEnd(v);
        return list;
    }

    private static void checkPointers(DoublyLinkedList list) {
        DDL_Node h = head(list);
        DDL_Node t = tail(list);

        if (h == null || t == null) {
            ok(h == null && t == null, "Si head es null, tail también debe ser null (y viceversa)");
            return;
        }

        ok(h.prev == null, "head.prev debe ser null");
        ok(t.next == null, "tail.next debe ser null");

        // adelante
        DDL_Node cur = h;
        DDL_Node last = null;
        int guard = 0;
        while (cur != null) {
            guard++;
            ok(guard < 10000, "Posible ciclo infinito en next");
            if (cur.next != null) ok(cur.next.prev == cur, "Enlace roto: next.prev no apunta al nodo actual");
            last = cur;
            cur = cur.next;
        }
        ok(last == t, "tail no coincide con el último nodo desde head");

        // atrás
        cur = t;
        DDL_Node first = null;
        guard = 0;
        while (cur != null) {
            guard++;
            ok(guard < 10000, "Posible ciclo infinito en prev");
            if (cur.prev != null) ok(cur.prev.next == cur, "Enlace roto: prev.next no apunta al nodo actual");
            first = cur;
            cur = cur.prev;
        }
        ok(first == h, "head no coincide con el primero desde tail");
    }

    // ===== TESTS =====

    public static void testEmptyListState() {
        DoublyLinkedList list = new DoublyLinkedList();
        ok(list.isEmpty(), "isEmpty() debe ser true en lista vacía");
        ok(head(list) == null, "head debe ser null en vacía");
        ok(tail(list) == null, "tail debe ser null en vacía");
        checkPointers(list);
    }

    public static void testDeleteOnEmptyList() {
        DoublyLinkedList list = new DoublyLinkedList();
        boolean r = list.deleteByValue(10);
        ok(!r, "deleteByValue en vacía debe retornar false");
        ok(list.isEmpty(), "Luego de borrar en vacía sigue vacía");
        checkPointers(list);
    }

    public static void testReverseEmptyList() {
        DoublyLinkedList list = new DoublyLinkedList();
        list.reverse();
        ok(list.isEmpty(), "reverse en vacía no debe cambiar");
        checkPointers(list);
    }

    public static void testInsertOneElement() {
        DoublyLinkedList list = new DoublyLinkedList();
        list.insertAtEnd(10);
        ok(!list.isEmpty(), "Luego de insertar, no debe estar vacía");
        ok(head(list) == tail(list), "Con 1 elemento: head == tail");
        eq("10", view(list), "Vista debe ser '10'");
        checkPointers(list);
    }

    public static void testInsertMultipleOrder() {
        DoublyLinkedList list = listOf(10, 20, 30);
        eq("10 20 30", view(list), "El orden debe respetarse");
        ok(head(list).data == 10, "head.data debe ser 10");
        ok(tail(list).data == 30, "tail.data debe ser 30");
        checkPointers(list);
    }

    public static void testDeleteSingleElement() {
        DoublyLinkedList list = listOf(10);
        boolean r = list.deleteByValue(10);
        ok(r, "Debe retornar true al borrar existente");
        ok(list.isEmpty(), "Luego de borrar el único elemento debe quedar vacía");
        ok(head(list) == null && tail(list) == null, "head y tail deben ser null");
        checkPointers(list);
    }

    public static void testDeleteHead() {
        DoublyLinkedList list = listOf(10, 20, 30);
        boolean r = list.deleteByValue(10);
        ok(r, "Debe borrar el head");
        eq("20 30", view(list), "Luego de borrar head debe quedar '20 30'");
        ok(head(list).prev == null, "Nuevo head.prev debe ser null");
        checkPointers(list);
    }

    public static void testDeleteTail() {
        DoublyLinkedList list = listOf(10, 20, 30);
        boolean r = list.deleteByValue(30);
        ok(r, "Debe borrar el tail");
        eq("10 20", view(list), "Luego de borrar tail debe quedar '10 20'");
        ok(tail(list).next == null, "Nuevo tail.next debe ser null");
        checkPointers(list);
    }

    public static void testDeleteMiddle() {
        DoublyLinkedList list = listOf(10, 20, 30);
        boolean r = list.deleteByValue(20);
        ok(r, "Debe borrar el nodo intermedio");
        eq("10 30", view(list), "Luego de borrar medio debe quedar '10 30'");
        DDL_Node h = head(list);
        ok(h.next != null && h.next.data == 30, "10.next debe ser 30");
        ok(h.next.prev == h, "30.prev debe ser 10");
        checkPointers(list);
    }

    public static void testDeleteNonExisting() {
        DoublyLinkedList list = listOf(10, 20, 30);
        boolean r = list.deleteByValue(99);
        ok(!r, "Borrar inexistente debe retornar false");
        eq("10 20 30", view(list), "No debe cambiar la lista");
        checkPointers(list);
    }

    public static void testReverseSingleElement() {
        DoublyLinkedList list = listOf(10);
        list.reverse();
        eq("10", view(list), "Reverse con 1 elemento queda igual");
        ok(head(list) == tail(list), "head == tail con 1 elemento");
        checkPointers(list);
    }

    public static void testReverseMultiple() {
        DoublyLinkedList list = listOf(10, 20, 30, 40);
        list.reverse();
        eq("40 30 20 10", view(list), "Reverse debe invertir el orden");
        ok(head(list).data == 40, "Nuevo head debe ser 40");
        ok(tail(list).data == 10, "Nuevo tail debe ser 10");
        checkPointers(list);
    }

    // ✅ TC-DLL-013: Search en lista vacía
    public static void testSearchOnEmptyList() {
        DoublyLinkedList list = new DoublyLinkedList();
        int pos = list.search(10);
        ok(pos == -1, "Search en lista vacía debe retornar -1");
        checkPointers(list);
    }

    // ✅ TC-DLL-014: Search inexistente
    public static void testSearchNonExisting() {
        DoublyLinkedList list = listOf(10, 20, 30);
        int pos = list.search(99);
        ok(pos == -1, "Search inexistente debe retornar -1");
        eq("10 20 30", view(list), "Search no debe modificar la lista");
        checkPointers(list);
    }

    // ========= runner =========
    public static TestSummary runAll() {
        int failed = 0;

        failed += run("TC-DLL-001 Estado lista vacía", DoublyLinkedListTest::testEmptyListState) ? 0 : 1;
        failed += run("TC-DLL-002 Delete en vacía", DoublyLinkedListTest::testDeleteOnEmptyList) ? 0 : 1;
        failed += run("TC-DLL-003 Reverse en vacía", DoublyLinkedListTest::testReverseEmptyList) ? 0 : 1;
        failed += run("TC-DLL-004 Insert 1 elemento", DoublyLinkedListTest::testInsertOneElement) ? 0 : 1;
        failed += run("TC-DLL-005 Insert múltiples orden", DoublyLinkedListTest::testInsertMultipleOrder) ? 0 : 1;
        failed += run("TC-DLL-006 Delete único", DoublyLinkedListTest::testDeleteSingleElement) ? 0 : 1;
        failed += run("TC-DLL-007 Delete head", DoublyLinkedListTest::testDeleteHead) ? 0 : 1;
        failed += run("TC-DLL-008 Delete tail", DoublyLinkedListTest::testDeleteTail) ? 0 : 1;
        failed += run("TC-DLL-009 Delete medio", DoublyLinkedListTest::testDeleteMiddle) ? 0 : 1;
        failed += run("TC-DLL-010 Delete inexistente", DoublyLinkedListTest::testDeleteNonExisting) ? 0 : 1;
        failed += run("TC-DLL-011 Reverse 1", DoublyLinkedListTest::testReverseSingleElement) ? 0 : 1;
        failed += run("TC-DLL-012 Reverse varios", DoublyLinkedListTest::testReverseMultiple) ? 0 : 1;
        failed += run("TC-DLL-013 Search en vacía", DoublyLinkedListTest::testSearchOnEmptyList) ? 0 : 1;
        failed += run("TC-DLL-014 Search inexistente", DoublyLinkedListTest::testSearchNonExisting) ? 0 : 1;

        int total = 14;
        int passed = total - failed;
        return new TestSummary(total, passed, failed);
    }

    private static boolean run(String name, Runnable test) {
        try {
            test.run();
            System.out.println("[PASSED] " + name);
            return true;
        } catch (Throwable e) {
            System.out.println("[FAILED] " + name + " -> " + e.getMessage());
            return false;
        }
    }

    public static class TestSummary {
        public final int total, passed, failed;
        public TestSummary(int total, int passed, int failed) {
            this.total = total; this.passed = passed; this.failed = failed;
        }
    }
}
