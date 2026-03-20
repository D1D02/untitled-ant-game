package components;

public class FoodComponent implements Component {
    int capacity;
    int food;
    float consuming_timer;
    float last_consumed;


    boolean isFoodEnded(float deltaTime) {

        this.last_consumed += deltaTime;

        if( this.last_consumed > this.consuming_timer ){
            this.last_consumed -= this.consuming_timer;
            if(this.food == 0) return true;
        }

        return false;

    }

    int getFood() {
        return this.food;
    }

    int addFood(float inc_food) {
        return (int) ((inc_food + this.food) - capacity);
    } 

    public void setFood(int food) {
        this.food = food;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setConsuming_timer(float consuming_timer) {
        this.consuming_timer = consuming_timer;
    }

    public void setLast_consumed(float last_consumed) {
        this.last_consumed = last_consumed;
    }

}
