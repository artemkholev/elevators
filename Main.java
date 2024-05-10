import java.util.Scanner;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        System.out.print("Введите количество этажей в доме: ");
        int countFloor = in.nextInt();

        HighRiseBuilding highRiseBuilding = new HighRiseBuilding(countFloor);
        // создание 3 потоков
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(3);

        // запуск 3 потоков
        executorService.scheduleAtFixedRate(Request::generatePassenger, 0, 2, TimeUnit.SECONDS);
        executorService.scheduleAtFixedRate(highRiseBuilding::actionsFirstElevator, 0, 1, TimeUnit.SECONDS);
        executorService.scheduleAtFixedRate(highRiseBuilding::actionsSecondElevator, 0, 1, TimeUnit.SECONDS);
    }

}