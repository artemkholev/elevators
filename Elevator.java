import java.util.*;
import java.util.stream.Stream;

public class Elevator {
    private short id;
    private short orientation;
    private short orientationGoal;
    private int position; ///< текщее положение
    private int target; ///< куда лифт движется
    private int maxCountPeople = 5; ///< максимальная грузоподъёмность
    private int countPeople = 0;
    private Map<Short, Integer> passengers = new HashMap<>();
    private Map<Integer, List<Request>> queuePassengers = new HashMap<>();

    public Elevator(short id) {
        this.id = id;
        this.target = 0;
        this.position = 1;
        this.orientation = 0;
    }

    public int getCountPeople() {
        return this.countPeople;
    }

    public void setCountPeople(int countPeople) {
        this.countPeople = countPeople;
    }

    public short getId() {
        return this.id;
    }

    public int getMaxCountPeople() {
        return this.maxCountPeople;
    }

    public short getOrientationGoal() {
        return this.orientationGoal;
    }

    public void setOrientationGoal(short orientationGoal) {
        this.orientationGoal = orientationGoal;
    }

    public Map<Short, Integer> getPassengers() {
        return this.passengers;
    }

    public void setPassengers(Map<Short, Integer> passengers) {
        this.passengers = passengers;
    }

    public Map<Integer, List<Request>> getQueuePassengers() {
        return this.queuePassengers;
    }

    public short getOrientation() {
        return this.orientation;
    }

    public void setOrientation(short orientation) {
        this.orientation = orientation;
    }

    public synchronized int getPosition(){
        return this.position;
    }

    public synchronized int getTarget(){
        return this.target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public void up(){
        this.position++;
    }

    public void down() {
        this.position--;
    }

    public boolean checkCountPeople() {
        return getMaxCountPeople() >= 1 + getCountPeople();
    }

    public void addPassengerCount() {
        setCountPeople(getCountPeople() + 1);
    }

    public void removePassengerCount() {
        setCountPeople(getCountPeople() - 1);
    }

    public static void passengersWayIn(Elevator elevator) {
        // если лифт не движется, пустой, выходим из функции, заявок нет
        if (elevator.getOrientation() == 0) {
            return;
        }

        final int elevatorPosition = elevator.getPosition();
        // если на этаже, где побывал лифт нет очереди заявок, создание
        if (!elevator.getQueuePassengers().containsKey(elevatorPosition)) {
            elevator.getQueuePassengers().put(elevatorPosition, new ArrayList<>());
        }

        List<Request> requests = elevator.getQueuePassengers().get(elevator.getPosition());

        for (int index = 0; index < requests.size(); index++) {

            if (!elevator.checkCountPeople()) {
                System.out.println("П-" + requests.get(index).getId() + " не смог зайти, лифт заполнен");
                return;
            }

            elevator.addPassengerCount();
            elevator.getPassengers().put(requests.get(index).getId(), requests.get(index).getTarget());
            elevator.setOrientation(requests.get(index).getOrientationGoal());

            if (elevator.getTarget() < requests.get(index).getTarget() && elevator.getOrientation() == 1) {
                elevator.setTarget(requests.get(index).getTarget());
            } else if (elevator.getTarget() > requests.get(index).getTarget() && elevator.getOrientation() == -1) {
                elevator.setTarget(requests.get(index).getTarget());
            }

            System.out.println("+ П-" + requests.get(index).getId() + " зашёл в лифт " + elevator.getId() + ", этаж " + requests.get(index).getPosition() + " -> " + requests.get(index).getTarget());
            requests.remove(index);
        }

        if (elevator.getQueuePassengers().get(elevator.getPosition()).isEmpty()) {
            elevator.getQueuePassengers().remove(elevator.getPosition());
        }
    }

    public static void passengersWayOut(Elevator elevator) {
        // если лифт не движется, пустой, выходим из функции
        if (elevator.getOrientation() == 0) {
            return;
        }

        // убираем тех пассажиров, которым нужно выйти
        Map<Short, Integer> passengers = elevator.getPassengers();
        int position = elevator.getPosition();
        passengers.entrySet().removeIf(elem -> {
            if (elem.getValue().equals(position)) {
                System.out.println("- П-" + elem.getKey() + " вышел на " + elem.getValue() + " этаже, лифт " + elevator.getId());
                elevator.removePassengerCount();
                return true;
            }
            return false;
        });

        // если лифт пустой останавливаем
        if (elevator.getPassengers().isEmpty() && elevator.getQueuePassengers().isEmpty()) {
            elevator.setOrientation((short) 0);
        }
    }
}