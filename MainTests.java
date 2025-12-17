public class MainTests {
    public static void main(String[] args) {
        DoublyLinkedListTest.TestSummary s = DoublyLinkedListTest.runAll();

        System.out.println("\n==== RESUMEN ====");
        System.out.println("Total pruebas:  " + s.total);
        System.out.println("Pasadas:       " + s.passed);
        System.out.println("Fallidas:      " + s.failed);
    }
}
