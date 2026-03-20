package components;

public class AliveComponent {
    float ttl;

    public void setTtl(float ttl) {
        this.ttl = ttl;
    }

    public float getTtl() {
        return this.ttl;
    }

    public boolean isDead( float deltaTime ) {
        this.ttl -= deltaTime;
        return this.ttl <= 0 ? true : false;        
    }
}
