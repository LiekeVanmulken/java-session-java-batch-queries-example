import entities.Record;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;


public class Main {
    public static void main(String[] args) {

        int records = 20000;
        int batchSize = 50;

        Main main = new Main();

        long durationSingleQueries = main.runTimedSingleQueries(records);
        long durationBatchQueries = main.runTimedBatchQueries(records, batchSize);

        System.out.println("Time single queries : " + durationSingleQueries + "ms");
        System.out.println("Time batched queries: " + durationBatchQueries + "s");

        PersistenceManager.INSTANCE.close();
    }

    /**
     * Used to divide the timer from nanoseconds to seconds
     */
    private static final long timeDivider = 1000000000;

    public long runTimedSingleQueries(int records) {
        long startTime = System.nanoTime();

        runSingleQueries(records);

        long endTime = System.nanoTime();
        return (endTime - startTime) / timeDivider;
    }

    public long runTimedBatchQueries(int records, int batchSize) {
        long startTime = System.nanoTime();

        runBatchQuery(records, batchSize);

        long endTime = System.nanoTime();
        return (endTime - startTime) / timeDivider;
    }

    private void runSingleQueries(int records) {

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();

        for (int i = 0; i < records; i++) {
            entityTransaction.begin();
            Record record = new Record("random data");
            entityManager.persist(record);
            entityTransaction.commit();
        }
        entityManager.clear();
        entityManager.close();
    }

    private void runBatchQuery(int records, int batchSize) {


        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();

        try {
            entityTransaction.begin();

            for (int i = 0; i < records; i++) {
                if (i > 0 && i % batchSize == 0) {
                    entityTransaction.commit();
                    entityTransaction.begin();
                    entityManager.clear();
                }

                Record record = new Record("random data");
                entityManager.persist(record);
            }

            entityTransaction.commit();
        } catch (RuntimeException e) {
            if (entityTransaction.isActive()) {
                entityTransaction.rollback();
            }
            throw e;
        } finally {
            entityManager.clear();
            entityManager.close();
        }
    }
}
