package bots;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

public class TheBot extends Bot {

	String name; // contains name for bot
	BotHelper helper = new BotHelper();// calls the bot helper class
	Image up, down, left, right, current;// creates the images
	String nextMessage = null;// says the message is null
	String message;
	String messageID = "empty";
	private double x, y;

	private BotState state = BotState.DODGE;

	enum BotState {
		SHOOT, DODGE, FORAGE
	}

	public TheBot() {
	}

	@Override
	public void newRound() {
		// TODO Auto-generated method stub
		state = BotState.DODGE;
	}

	@Override
	public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
		int move = 0; // the int that is being retured to arena currently just a placeholder will
		// later be filled with more info
		// booleans to allow movement for follow, shoot, and retrieve, will not work if
		// out of bullets then other code runs or needs to dodge
		boolean bottom = false;
		boolean top = false;
		boolean canBottomTop = true;
		boolean canRetrive = true;

		// bot helper to find closest enemy
		BotInfo closestEnemy = helper.findClosest(me, liveBots);
		// if there are bullets
		if (bullets.length != 0) {
			// finds the closest bullet
			Bullet closestDanger = helper.findClosest(me, bullets);
		}

		if (state == BotState.SHOOT) {
			Bullet bullet = closestDanger(me, bullets, x, y);
			if (bullet != null) {
				state = BotState.DODGE;
			}

			// if I am closer to the bottom move down
			if ((me.getY() > (BattleBotArena.BOTTOM_EDGE / 2)) && (canBottomTop == true)) {
				move = BattleBotArena.DOWN;
			}
			// if I am closer to the top move up
			else if ((me.getY() < (BattleBotArena.BOTTOM_EDGE / 2)) && (canBottomTop == true)) {
				move = BattleBotArena.UP;
			}
			// if I am at the top, allow other code to run
			if ((me.getY() <= (BattleBotArena.TOP_EDGE + 10)) && (canBottomTop == true)) {
				canBottomTop = false;
				top = true;
			}
			// if I am the bottom, allow other code to runl
			if ((me.getY() >= (BattleBotArena.BOTTOM_EDGE - 30)) && (canBottomTop == true)) {
				canBottomTop = false;
				bottom = true;
			}

			// if the enemy bot is to the right of me move right
			if ((closestEnemy.getX() > me.getX()) && (bottom == true || top == true)) {
				move = BattleBotArena.RIGHT;
			}
			// if the enemy bot is to the left of me move left
			else if ((closestEnemy.getX() < me.getX()) && (bottom == true || top == true)) {
				move = BattleBotArena.LEFT;
			}
			
			//make sure the bot you're attacking isn't on your team
			if (closestEnemy.getName() != "freeRangeBot2" && closestEnemy.getName() != "freeRangeBot3"
				&& closestEnemy.getTeamName() != "HAS") {
				
				//System.out.println("1 " + closestEnemy.getName());
				// if I have the same general location as the enemy bot shoot up
				if ((Math.abs(me.getX() - closestEnemy.getX()) < Bot.RADIUS) && (bottom == true)) {
					move = BattleBotArena.FIREUP;
				}
				// if I have the same location as the enemy bot shoot down
				if ((Math.abs(me.getX() - closestEnemy.getX()) < Bot.RADIUS) && (top == true)) {
					move = BattleBotArena.FIREDOWN;
				}
			}
			
		}

		else if (state == BotState.DODGE) {
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
					} else if (Math.abs(me.getY() - bullet.getY()) < Bot.RADIUS * 3
							&& me.getX() - bullet.getX() <= 50) {

						move = BattleBotArena.DOWN;

						if (Math.abs(me.getY() - BattleBotArena.BOTTOM_EDGE) <= 50) {
							move = BattleBotArena.UP;
						}
					} else if (Math.abs(me.getX() - bullet.getX()) < Bot.RADIUS * 3
							&& me.getY() - bullet.getY() >= -50) {

						move = BattleBotArena.RIGHT;

						if (Math.abs(me.getX() - BattleBotArena.RIGHT_EDGE) <= 50) {
							move = BattleBotArena.LEFT;
						}
					} else if (Math.abs(me.getY() - bullet.getY()) < Bot.RADIUS * 3
							&& me.getX() - bullet.getX() >= -50) {

						move = BattleBotArena.UP;

						if (Math.abs(me.getY() - BattleBotArena.TOP_EDGE) <= 50) {
							move = BattleBotArena.DOWN;
						}
					}
				} else {
					state = BotState.SHOOT;
				}
			}

		} else if (state == BotState.FORAGE) {
			Bullet bullet = closestDanger(me, bullets, x, y);
			if (bullet != null) {
				state = BotState.DODGE;
			}

			if (deadBots.length != 0) {
				// finds the closes dead bot
				BotInfo closestDead = helper.findClosest(me, deadBots);
				// if there is only 1 bullet left the bot will stop all other actions and go to
				// the closest dead bot
				if (canRetrive == true) {
					if (me.getBulletsLeft() <= 0) {
						// the movement code to move to the closest dead bot if the dead bot has bullets
						// on it
						if (closestDead.getBulletsLeft() >= 1) {
							// stops all other forms of movement
							bottom = false;
							top = false;
							canBottomTop = false;
							if (Math.abs(me.getX() - closestDead.getX()) < Bot.RADIUS) {
								if (closestDead.getY() > me.getY()) {
									move = BattleBotArena.DOWN;
								} else if (closestDead.getY() < me.getY()) {
									move = BattleBotArena.UP;
								}
							} else if (closestDead.getX() > me.getX()) {
								move = BattleBotArena.RIGHT;
							} else if (closestDead.getX() < me.getX()) {
								move = BattleBotArena.LEFT;
							}
						}
					}
					// if I have any bullets allow the process for movement to begin again
					if (me.getBulletsLeft() > 0) {
						canBottomTop = true;
					}
				}
			}
		}
		// return the move commands

		x = me.getX();
		y = me.getY();

		return move;

	}

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
		g.setColor(Color.cyan);
		g.fillRect(x + 2, y + 2, RADIUS * 2 - 4, RADIUS * 2 - 4);

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
//		if (name == null)
//			name = "freeRangeBot";
		return "freeRangeBot";
	}

	@Override
	public String getTeamName() {
//		String message = messageID;
//		return message;
		return "HAS";
	}

	@Override
	public String outgoingMessage() {
		// TODO Auto-generated method stub
		String msg = nextMessage;
		nextMessage = null;
		return msg;
	}

	@Override
	public void incomingMessage(int botNum, String msg) {
	}

	@Override
	public String[] imageNames() {
		String[] images = { "heart_up.png", "heart_down.png", "heart_left.png", "heart_right.png" };
		return images;
	}

	@Override
	public void loadedImages(Image[] images) {
		if (images != null) {
			current = up = images[0];
			down = images[1];
			left = images[2];
			right = images[3];
		}
	}

}
