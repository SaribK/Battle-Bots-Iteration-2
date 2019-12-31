package bots;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;
import bots.TheBot2.BotState;

public class OtherBot extends Bot {

	BotHelper helper = new BotHelper();
	public int move;
	public double xDist, yDist;
	public double absXDist, absYDist;
	public double enemyTimeX, enemyTimeY;
	public double bulletTimeX, bulletTimeY;
	public double xDistance, yDistance;
	public double x, y;
	public int spiralCounter = 0;

	private BotState state = BotState.DODGE;

	Image current;

	enum BotState {
		SHOOT, DODGE, FORAGE, START
	}

	public OtherBot() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void newRound() {
		// TODO Auto-generated method stub
		state = BotState.DODGE;
	}

	@Override
	public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
		System.out.println(state);

//		// spiralCounter++;
//
//		if (spiralCounter <= 3) {
//
//			if (state == BotState.START) {
//				// spiralCounter++;
//				//if (me.getBulletsLeft() > 0) {
//					if (spiralCounter == 0) {
//						spiralCounter++;
//						return BattleBotArena.FIREUP;
//					} else if (spiralCounter == 1) {
//						spiralCounter++;
//						return BattleBotArena.FIRERIGHT;
//					} else if (spiralCounter == 2) {
//						spiralCounter++;
//						return BattleBotArena.FIREDOWN;
//					}
//					else if (spiralCounter == 3) {
//						state = BotState.DODGE;
//						return BattleBotArena.FIRELEFT;
//					}
//				//}
//			}
//		} else {

			if (state == BotState.DODGE) {
				dodge(me, liveBots, bullets);
			}

			else if (state == BotState.SHOOT) {
				preFire(me, shotOK, liveBots, deadBots, bullets);
			}

			else if (state == BotState.FORAGE) {
				findBullets(me, deadBots, bullets, liveBots);
			}
		//}

		return move;
	}

	
	// THIS IS THE DODGE FUNCTION
	private int dodge(BotInfo me, BotInfo[] liveBots, Bullet[] bullets) {
		// if there are bullets in range
		if (bullets.length > 0) {
			// get the closest bullet with made function
			Bullet bullet = closestDanger(me, bullets, x, y);
			// if bullets are in the world
			if (bullet != null) {
				// checks all four directions to see where bullet is approaching for
				if (Math.abs(me.getX() - bullet.getX()) < Bot.RADIUS * 3 && me.getY() - bullet.getY() <= 50) {
					// if bot is on the left of bullet

					// move left
					move = BattleBotArena.LEFT;

					// if bot is on the right of bullet

					// if bot is close to the left edge, move right
					if (Math.abs(me.getX() - BattleBotArena.LEFT_EDGE) <= 50) {
						move = BattleBotArena.RIGHT;
					}
				} else if (Math.abs(me.getY() - bullet.getY()) < Bot.RADIUS * 3 && me.getX() - bullet.getX() <= 50) {

					move = BattleBotArena.DOWN;

					if (Math.abs(me.getY() - BattleBotArena.BOTTOM_EDGE) <= 50) {
						move = BattleBotArena.UP;
					}
				} else if (Math.abs(me.getX() - bullet.getX()) < Bot.RADIUS * 3 && me.getY() - bullet.getY() >= -50) {

					move = BattleBotArena.RIGHT;

					if (Math.abs(me.getX() - BattleBotArena.RIGHT_EDGE) <= 50) {
						move = BattleBotArena.LEFT;
					}
				} else if (Math.abs(me.getY() - bullet.getY()) < Bot.RADIUS * 3 && me.getX() - bullet.getX() >= -50) {

					move = BattleBotArena.UP;

					if (Math.abs(me.getY() - BattleBotArena.TOP_EDGE) <= 50) {
						move = BattleBotArena.DOWN;
					}
				}
			} else {
				state = BotState.SHOOT;
			}
		}
		return move;
	}

	// THIS IS THE PRE-FIRE FUNCTION
	private int preFire(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
		// move = 0;
		BotInfo closestEnemy = helper.findClosest(me, liveBots);

		if (shotOK == true && closestEnemy.getName() != "freeRangeBot1" && closestEnemy.getName() != "freeRangeBot2"
				&& closestEnemy.getTeamName() != "HAS") {

			// the difference of the distance btwn mybot and the enemy
			xDist = closestEnemy.getX() - me.getX() + Bot.RADIUS;
			yDist = closestEnemy.getY() - me.getY() + Bot.RADIUS;

			// the absolute value of the distance
			absXDist = Math.abs(xDist);
			absYDist = Math.abs(yDist);

			// the time the bullet's x and y takes to get to the enemy position
			bulletTimeX = absXDist / BattleBotArena.BULLET_SPEED;
			bulletTimeY = absYDist / BattleBotArena.BULLET_SPEED;

			// the time the enemy's x and y takes to get to the bot position
			enemyTimeX = absXDist / BattleBotArena.BOT_SPEED;
			enemyTimeY = absYDist / BattleBotArena.BOT_SPEED;

			// if the bullet time is greater than or equal to the enemy time:
			if (enemyTimeX <= bulletTimeY) {

				// if the dist is -ive: (the enemy is below our bot)
				if (yDist < 0) {
					return BattleBotArena.FIREUP;
				}

				// if the dist is +ive: (the enemy is above our bot)
				if (yDist > 0) {
					return BattleBotArena.FIREDOWN;
				}
			}

			// if the bullet time is greater than or equal to the enemy time:
			if (enemyTimeY <= bulletTimeX) {

				// if the dist is -ive: (the enemy is below our bot)
				if (xDist < 0) {
					return BattleBotArena.FIRELEFT;
				}

				// if the dist is +ive: (the enemy is above our bot)
				if (xDist > 0) {
					return BattleBotArena.FIRERIGHT;
				}
			}
		} else {
			state = BotState.FORAGE;
		}
		return move;
	}

	// THIS IS THE BULLET-FINDING FUNCTION
	public int findBullets(BotInfo me, BotInfo[] deadBots, Bullet[] bullets, BotInfo[] liveBots) {
		// if there are dead bots, then find the closest one
		if (deadBots.length > 0) {
			BotInfo closestDead = helper.findClosest(me, deadBots);

			// see if the bot has any bullets left in him
			if (closestDead.getBulletsLeft() > 0) {

				// get the x and y distance,
				xDistance = me.getX() - closestDead.getX();
				yDistance = me.getY() - closestDead.getY();

				// move towards it
				// if the xdist. is -ive, that means it needs to move right
				if (xDistance < -4) {
					move = BattleBotArena.RIGHT;
					// current = right;
				}
				// if the xdist. is +ive, that means it needs to move left
				else if (xDistance > 4) {
					move = BattleBotArena.LEFT;
					// current = left;
				} else if (-3 < xDistance && xDistance < 3) {
					// if the ydist. is -ive, that means it needs to move down
					if (yDistance < 0) {
						move = BattleBotArena.DOWN;
						// current = down;
					}
					// if the ydist. is +ive, that means it needs to move up
					else {
						move = BattleBotArena.UP;
						// current = up;
					}

				}

			}
		} else {
			state = BotState.DODGE;
		}
		return move;
	}

	// THIS IS THE FINDING CLOSEST DANGER CLASS
	private Bullet closestDanger(BotInfo me, Bullet[] bullets, double x, double y) {
		// variables for bullet and closest bullet
		Bullet danger = null;
		Bullet closestDanger = null;
		// goes through all bullets
		for (int i = 0; i < bullets.length; i++) {
			danger = bullets[i];
			// if bullet is approaching from above
			if (Math.abs(me.getX() - danger.getX()) < Bot.RADIUS * 3 && me.getY() - danger.getY() <= 0
					&& danger.getYSpeed() < 0) {
				// if the new bullet is closer than the previous bullet
				if (closestDanger == null || closestDanger.getY() > danger.getY()) {
					closestDanger = danger;
				}
			}
			// if bullet is approaching from below
			if (Math.abs(me.getX() - danger.getX()) < Bot.RADIUS * 3 && me.getY() - danger.getY() >= 0
					&& danger.getYSpeed() > 0) {
				if (closestDanger == null || closestDanger.getY() < danger.getY()) {
					closestDanger = danger;
				}
			}
			// if bullet is approaching from left
			if (Math.abs(me.getY() - danger.getY()) < Bot.RADIUS * 3 && me.getX() - danger.getX() >= 0
					&& danger.getXSpeed() > 0) {
				if (closestDanger == null || closestDanger.getX() < danger.getX()) {
					closestDanger = danger;
				}
			}
			// if bullet is approaching from right
			if (Math.abs(me.getY() - danger.getY()) < Bot.RADIUS * 3 && me.getX() - danger.getX() <= 0
					&& danger.getXSpeed() < 0) {
				if (closestDanger == null || closestDanger.getX() > danger.getX()) {
					closestDanger = danger;
				}
			}
			// if bullet and bot are approaching from different axis (e.g bot in x, bullet
			// in y)
			if (Math.abs(me.getX() - danger.getX()) <= 50 && Math.abs(me.getY() - danger.getY()) <= 50) {
				// if bot goes right and bullet goes up
				if (me.getY() < danger.getY() && danger.getYSpeed() < 0 && me.getX() < danger.getX()
						&& me.getX() - x > 0) {
					if (closestDanger == null
							|| helper.calcDistance(me.getX(), me.getY(), danger.getX(), danger.getY()) < helper
									.calcDistance(me.getX(), me.getY(), closestDanger.getX(), closestDanger.getY())) {
						closestDanger = danger;
					}
				}
				// if bot goes left and bullet goes up
				else if (me.getY() < danger.getY() && danger.getYSpeed() < 0 && me.getX() > danger.getX()
						&& me.getX() - x < 0) {
					if (closestDanger == null
							|| helper.calcDistance(me.getX(), me.getY(), danger.getX(), danger.getY()) < helper
									.calcDistance(me.getX(), me.getY(), closestDanger.getX(), closestDanger.getY())) {
						closestDanger = danger;
					}
				}
				// if bot goes down and bullet goes left
				else if (me.getY() < danger.getY() && danger.getXSpeed() < 0 && me.getX() < danger.getX()
						&& me.getY() - y > 0) {
					if (closestDanger == null
							|| helper.calcDistance(me.getX(), me.getY(), danger.getX(), danger.getY()) < helper
									.calcDistance(me.getX(), me.getY(), closestDanger.getX(), closestDanger.getY())) {
						closestDanger = danger;
					}
				}
				// if bot goes down and bullet goes right
				else if (me.getY() < danger.getY() && danger.getXSpeed() > 0 && me.getX() > danger.getX()
						&& me.getY() - y > 0) {
					if (closestDanger == null
							|| helper.calcDistance(me.getX(), me.getY(), danger.getX(), danger.getY()) < helper
									.calcDistance(me.getX(), me.getY(), closestDanger.getX(), closestDanger.getY())) {
						closestDanger = danger;
					}
				}
				// if the bot goes right and bullet goes down
				else if (me.getY() > danger.getY() && danger.getYSpeed() > 0 && me.getX() < danger.getX()
						&& me.getX() - x > 0) {
					closestDanger = danger;
				}
				// bot goes left and bullet goes down
				else if (me.getY() > danger.getY() && danger.getYSpeed() > 0 && me.getX() > danger.getX()
						&& me.getX() - x < 0) {
					closestDanger = danger;
				}
				// bot goes up and bullet goes left
				else if (me.getY() > danger.getY() && danger.getXSpeed() < 0 && me.getX() < danger.getX()
						&& me.getY() - y < 0) {
					closestDanger = danger;
				}
				// bot goes up and bullet goes right
				else if (me.getY() > danger.getY() && danger.getXSpeed() > 0 && me.getX() > danger.getX()
						&& me.getY() - y < 0) {
					closestDanger = danger;
				}
			}
		}
		return closestDanger;
	}

	@Override
	public void draw(Graphics g, int x, int y) {
		// TODO Auto-generated method stub
		g.drawImage(current, x, y, Bot.RADIUS * 2, Bot.RADIUS * 2, null);
		g.setColor(Color.MAGENTA);
		g.fillRect(x + 2, y + 2, RADIUS * 2 - 4, RADIUS * 2 - 4);

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "freeRangeBot3";
	}

	@Override
	public String getTeamName() {
		// TODO Auto-generated method stub
		return "HAS";
	}

	@Override
	public String outgoingMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void incomingMessage(int botNum, String msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public String[] imageNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void loadedImages(Image[] images) {
		// TODO Auto-generated method stub

	}

}
