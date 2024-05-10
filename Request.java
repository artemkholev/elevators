import java.util.concurrent.ThreadLocalRandom;

public class Request {
    private int target, position;
    private int weight;
    private short id, orientationGoal;
    static short idPassenger = 0;

    public Request(int currentFloor, int targetFloor, short id, int weight) {
        this.id = id;
        this.target = targetFloor;
        this.position = currentFloor;
        this.weight = weight;

        if (targetFloor < currentFloor) {
            this.orientationGoal = -1;
        } else if (targetFloor > currentFloor) {
            this.orientationGoal = 1;
        } else {
            this.orientationGoal = 0;
        }
    }

    public int getWeight() {
        return this.weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getTarget() {
        return this.target;
    }

    public short getOrientationGoal() {
        return this.orientationGoal;
    }

    public int getPosition() {
        return this.position;
    }

    public short getId() {
        return this.id;
    }

    private static int getRandomFloor() {
        return ThreadLocalRandom.current().nextInt(1, HighRiseBuilding.countFloor + 1);
    }

    private static int getRandomWeight() {
        return ThreadLocalRandom.current().nextInt(20, 100);
    }

    public static void generatePassenger() {
        int position = getRandomFloor();
        int target = getRandomFloor();
        int weight = getRandomWeight();

        while (position == target) {
            target = getRandomFloor();
        }

        idPassenger++;
        Request request = new Request(position, target, idPassenger, weight);
        HighRiseBuilding.passengers.add(request);
        System.out.println("П-" + idPassenger + " на этаже " + position);
    }
}