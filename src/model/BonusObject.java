package model;

public class BonusObject extends ImmovableObject {

    private static final int SIZE = (GameModel.DISCRETE_FACTOR * 16) / 64;
    
    @Override
    public boolean attacked(Projectile p) {
        return true;
    }

    @Override
    public int getWidth() {
        return SIZE;
    }

    @Override
    public int getHeight() {
        return SIZE;
    }

    public enum Bonus {
        HEALTH_UP {
            @Override
            public void effect(GameModel model, Tank tank) {
                int hp = tank.getHealth(); 
                if (hp < 10) {
                    tank.setHealth(hp + 1);
                }
            }
        }, HEALTH_DOWN {
            @Override
            public void effect(GameModel model, Tank tank) {
                tank.setHealth(tank.getHealth() - 1);
            }
        }, GROW_TREES {
            @Override
            public void effect(GameModel model, Tank tank) throws ModelException {
                model.addRandomTrees(model.getWidth() / GameModel.DISCRETE_FACTOR * model.getHeight()  / GameModel.DISCRETE_FACTOR / 30);                
            }
        };
        
        public abstract void effect(GameModel model, Tank tank) throws ModelException;
    }
    
    public Bonus bonus;
    
    public BonusObject(int id, Vector2D p, GameObjectDescription d, Bonus bonus) {
        super(id, p, GameObjectDescription.BONUS);
        this.bonus = bonus;
    }

    public void effect(GameModel model, Tank tank) throws ModelException {
        bonus.effect(model, tank);
    }

    public static int getSize() {
        return SIZE;
    }
}
