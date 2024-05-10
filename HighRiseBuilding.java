import java.util.*;

public class HighRiseBuilding {
    final Elevator firstElevator = new Elevator((short) 1),
            secondElevator = new Elevator((short) 2);
    public static int countFloor;
    public static List<Request> passengers = new ArrayList<Request>();

    public HighRiseBuilding(int countFloor) {
        this.countFloor = countFloor;
    }

    private synchronized void action(Elevator elevator, Elevator elevatorCompare){
        short idElevator = elevator.getId();
        int positionElevator = elevator.getPosition();

        System.out.println("Лифт " + idElevator + " на этаже " + positionElevator);

        updateElevator(elevator);

        if (checkNearestElevator(elevator, elevatorCompare)) {
            return;
        }

        // если лифт имеет быстрее может добраться до пассажиров, при условии, что они есть можно изменить состояние
        // есть 2 варианта, лифт стоял или двигался
        if (!passengers.isEmpty()) {
            startElevator(elevator);
        }

        if (elevator.getOrientation() == 1 && positionElevator + 1 <= countFloor) {
            elevator.up();
        } else if (elevator.getOrientation() == -1 && positionElevator - 1 >= 1) {
            elevator.down();
        }
    }

    private synchronized void updateRequests(Elevator elevator) {
        final int orientationElevator = elevator.getOrientation();
        final int goalOrientationElevator = elevator.getOrientationGoal();

        if (orientationElevator == 0) {
            return;
        }

        for (int index = 0; index < passengers.size(); index++) {
            if ((passengers.get(index).getOrientationGoal() == orientationElevator || elevator.getPosition() == elevator.getTarget()) &&
                (passengers.get(index).getPosition() <= elevator.getPosition() && orientationElevator == -1
                || passengers.get(index).getPosition() >= elevator.getPosition() && orientationElevator == 1)) {
                if (!elevator.getQueuePassengers().containsKey(passengers.get(index).getPosition()))  {
                    elevator.getQueuePassengers().put(passengers.get(index).getPosition(), new ArrayList<>());
                }

                elevator.getQueuePassengers().get(passengers.get(index).getPosition()).add(passengers.get(index));
                passengers.remove(index);
            }
        }
    }

    private void startElevator(Elevator elevator) {
        Request request = passengers.remove(0);
        final int requestPosition = request.getPosition();
        elevator.setTarget(requestPosition);

        if (!elevator.getQueuePassengers().containsKey(requestPosition)) {
            elevator.getQueuePassengers().put(requestPosition, new ArrayList<>());
        }
        elevator.getQueuePassengers().get(requestPosition).add(request);

        if (requestPosition > elevator.getPosition()) {
            elevator.setOrientation((short) 1);
        } else {
            elevator.setOrientation((short) -1);
        }

        elevator.setOrientationGoal(request.getOrientationGoal());

        updateRequests(elevator);
        Elevator.passengersWayIn(elevator);
    }

    private void updateElevator(Elevator elevator) {
        Elevator.passengersWayOut(elevator);
        updateRequests(elevator);
        Elevator.passengersWayIn(elevator);
    }

    public boolean checkNearestElevator(Elevator elevator, Elevator elevatorCompare) {
        return (elevator.getOrientation() == 0 && elevatorCompare.getOrientation() == 0 && !passengers.isEmpty()) &&
                (Math.abs(elevatorCompare.getPosition() - passengers.get(0).getPosition()) < Math.abs(elevator.getPosition() - passengers.get(0).getPosition()));
    }

    public void actionsFirstElevator() {
        action(this.firstElevator, this.secondElevator);
    }

    public void actionsSecondElevator() {
        action(this.secondElevator, this.firstElevator);
    }
}