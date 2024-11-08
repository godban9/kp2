import java.util.ArrayList;                       
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Main {
    public static void main(String[] args) {
        // Введення числа N від користувача
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введiть число N для пошуку всіх простих чисел до N: ");
        int n = scanner.nextInt();

        // Кількість потоків для розподілу обчислень
        int numberOfThreads = 4;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        List<Future<List<Integer>>> futures = new ArrayList<>();
        CopyOnWriteArrayList<Integer> allPrimes = new CopyOnWriteArrayList<>();

        // Вимірюємо час роботи програми
        long startTime = System.currentTimeMillis();

        // Розділяємо діапазон [0; N] на частини для кожного потоку
        int rangePerThread = n / numberOfThreads;

        for (int i = 0; i < numberOfThreads; i++) {
            int start = i * rangePerThread;
            int end = (i == numberOfThreads - 1) ? n : (i + 1) * rangePerThread;

            // Створюємо завдання для пошуку простих чисел у діапазоні [start; end]
            PrimeFinder primeFinder = new PrimeFinder(start, end);
            Future<List<Integer>> future = executorService.submit(primeFinder);
            futures.add(future);
        }

        // Перевіряємо статус виконання завдань та збираємо результати
        for (Future<List<Integer>> future : futures) {
            try {
                if (future.isCancelled()) {
                    System.out.println("Завдання було відмінено.");
                } else {
                    // Отримуємо список простих чисел з потоку
                    List<Integer> primes = future.get(); 
                    System.out.println("Завдання виконано: " + future.isDone());
                    allPrimes.addAll(primes); // Додаємо прості числа до загального списку
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        // Завершуємо роботу ExecutorService
        executorService.shutdown();

        // Вимірюємо час закінчення роботи програми
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        // Сортуємо список простих чисел та виводимо результат
        allPrimes.sort(Integer::compareTo);
        System.out.println("Прості числа до " + n + ": " + allPrimes);

        // Вивід часу роботи програми
        System.out.println("Час роботи програми: " + totalTime + " мс");
    }
}

// Клас, який реалізує Callable для пошуку простих чисел
class PrimeFinder implements Callable<List<Integer>> {
    private final int start;
    private final int end;

    // Конструктор приймає початковий і кінцевий діапазон
    public PrimeFinder(int start, int end) {
        this.start = start;
        this.end = end;
    }

    // Метод call() реалізує логіку пошуку простих чисел
    @Override
    public List<Integer> call() {
        List<Integer> primes = new ArrayList<>();
        for (int i = Math.max(start, 2); i <= end; i++) {
            if (isPrime(i)) {
                primes.add(i);
            }
        }
        return primes;
    }

    // Метод для перевірки, чи є число простим
    private boolean isPrime(int num) {
        if (num <= 1) return false;
        for (int i = 2; i <= Math.sqrt(num); i++) {
            if (num % i == 0) return false;
        }
        return true;
    }
}
