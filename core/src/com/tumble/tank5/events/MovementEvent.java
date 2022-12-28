package com.tumble.tank5.events;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import com.tumble.tank5.util.Position;
import com.tumble.tank5.world_logic.game_n_world.GameWorld;
import com.tumble.tank5.game_object.entities.Entity;
import com.tumble.tank5.game_object.entities.Move;
import com.tumble.tank5.util.DirectionVector;

public class MovementEvent extends Event {
	/** <i>Must</i> be odd. */
	public static final int MOVEMENT_TICKS = 11;

	private enum MovementType {
		START, MIDDLE, END
	}

	private Map<Entity, Move> moves;

	private MovementType eventType;

	/**
	 * Constructs a new step of movement to be applied to all given
	 * <code>Entities</code> (i.e., all <code>Entities</code> who plan to move this
	 * <code>Round</code>). The start & end steps serve to lock each
	 * <code>Entity</code> into its server-determined start & end
	 * <code>Position</code> (at the centre of its <code>Tile</code>).
	 * 
	 * @param tickNumber - the tick at which the step should be applied. Should be
	 *                   in the range [0, {@link MovementEvent#MOVEMENT_TICKS}].
	 * 
	 * @param moves      - a <code>Map</code> of all the <code>Entities</code> who
	 *                   plan to move this turn to a <code>Move</code> object
	 *                   containing information about how/where to move to/from.
	 * 
	 * @param eventType  - {@link MovementType#START} locks each <code>Entity</code>
	 *                   into its {@link Move#start} <code>Position</code>,
	 *                   {@link MovementType#MIDDLE} locks each <code>Entity</code>
	 *                   into its {@link Move#start}
	 *                   <code>Position</code>,{@link MovementType#END} locks each
	 *                   <code>Entity</code> into its {@link Move#end}
	 *                   <code>Position</code> (unless its movement was
	 *                   interrupted).
	 */
	private MovementEvent(int tickNumber, Map<Entity, Move> moves, MovementType eventType) {
		super(tickNumber);
		
		this.moves = moves;
		this.eventType = eventType;
	}

	@Override
	public boolean applicable(GameWorld gW, int currentTick, int roundNumber) {
		return true;
	}

	@Override
	public void apply(GameWorld gW, int currentTick, Queue<Event> eventStream) {
		for (Entity key : moves.keySet()) {
			switch (eventType) {
			case START:
				moves.get(key).applyStart(key);
				break;
			case MIDDLE:
				moves.get(key).applyMiddle(key, gW, currentTick, eventStream);
				break;
			case END:
				moves.get(key).applyEnd(key);
				break;
			}
		}
		
		finished = true;
	}

	@Override
	public String toString() {
		String res = "{MovementEvent["
				+ tickNumber
				+ "] ("
				+ eventType
				+ "): ";
		
		for (Entity entity : moves.keySet())
			res += "<" + entity + ", " + moves.get(entity) + ">, ";

		return res.substring(0, res.length() - 2) + "}";
	}

	public static MovementEvent[] createMovementSeries(Map<Entity, DirectionVector> directions) {
		MovementEvent[] steps = new MovementEvent[MovementEvent.MOVEMENT_TICKS];

		// Make our own map of moves (rather than being given them directly) to maintain
		// encapsulation.
		Map<Entity, Move> moves = new HashMap<Entity, Move>();

		for (Entity key : directions.keySet()) {
			Position currentPos = key.getPosition().tileCentre();
			moves.put(
					key,
					new Move(
							directions.get(key).asEnum(),
							currentPos,
							currentPos.move(directions.get(key))));
		}

		steps[0] = new MovementEvent(0, moves, MovementType.START);

		for (int i = 1; i < MovementEvent.MOVEMENT_TICKS - 1; i++) {
			steps[i] = new MovementEvent(i, moves, MovementType.MIDDLE);
		}

		steps[MovementEvent.MOVEMENT_TICKS - 1] = new MovementEvent(
				MovementEvent.MOVEMENT_TICKS - 1,
				moves,
				MovementType.END);

		return steps;
	}
}
