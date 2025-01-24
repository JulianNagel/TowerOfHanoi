package ch.bbw.towerofhanoi.model;

import ch.bbw.towerofhanoi.model.exception.InvalidMoveException;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * A mutable representation of a state of the game Tower of Hanoi.
 * Each disk is represented as a unique integer from 0 (smallest) to N (largest).
 * Each peg is organized as a list, where the first item represents the disk lowest in the stack.
 *
 * @param pegA usually the starting location
 * @param pegB usually the auxiliary pole
 * @param pegC usually the target location
 */
@Component
public record HanoiBoard(LinkedList<Integer> pegA, LinkedList<Integer> pegB, LinkedList<Integer> pegC) {

	public HanoiBoard { // verified this to be a valid board
		Stream.of(pegA, pegB, pegC)
				.forEach(peg -> {
					if (!peg.stream().sorted(Comparator.reverseOrder()).toList().equals(peg)) {
						throw new IllegalStateException("Invalid peg: cannot stack large disks on small ones");
					}
				});
		var duplicates = Stream.concat(Stream.concat(pegA.stream(), pegB.stream()), pegC.stream())
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
				.entrySet()
				.stream()
				.filter(e -> e.getValue() > 1)
				.map(Map.Entry::getKey)
				.toList();
		if (!duplicates.isEmpty()) {
			throw new IllegalStateException("duplicate disks found: " + duplicates);
		}
	}

	/**
	 * Create a new board with randomly distributed disks.
	 *
	 * @param numberOfPieces amount of disks
	 * @return a new random board
	 */
	public static HanoiBoard randomWithSize(int numberOfPieces) {
		var board = initializeWithSize(numberOfPieces);
		for (var i = board.pegA.iterator(); i.hasNext(); ) {
			var disk = i.next();
			var target = Peg.values()[new Random().nextInt(3)];
			if (target != Peg.A) {
				i.remove();
				board.byPeg(target).addLast(disk);
			}
		}
		return board;
	}

	/**
	 * Create a new board with all disks on peg A.
	 *
	 * @param numberOfPieces amount of disks
	 * @return a new default board
	 */
	public static HanoiBoard initializeWithSize(int numberOfPieces) {
		if (numberOfPieces <= 0) {
			throw new IllegalArgumentException("must use at least one disk");
		}
		// fill peg A from largest disk first to the smallest last: [N,... 2,1,0]
		// all other pegs remain empty
		var pegA = new LinkedList<>(IntStream.iterate(numberOfPieces - 1, i -> i - 1).limit(numberOfPieces).boxed().toList());
		return new HanoiBoard(pegA, new LinkedList<>(), new LinkedList<>()).copy();
	}

	/**
	 * @return true in case all disks are on peg C.
	 */
	public boolean isSolved() {
		return pegA.isEmpty() && pegB.isEmpty();
	}

	/**
	 * @return the total amount of disks.
	 */
	public int getNumberOfPieces() {
		return pegA.size() + pegB.size() + pegC.size();
	}

	/**
	 * @param diskSize the index of the disk to search
	 * @return on which peg the disk currently resides (or an exception if no such disk exist)
	 */
	public Peg getPegOfDiskWithIndex(int diskSize) {
		if (pegA.contains(diskSize)) {
			return Peg.A;
		}
		if (pegB.contains(diskSize)) {
			return Peg.B;
		}
		if (pegC.contains(diskSize)) {
			return Peg.C;
		}
		throw new IllegalStateException("there is no disk with size " + diskSize);
	}

	/**
	 * @param peg index of the peg (A, B, C).
	 * @return the corresponding list of disks.
	 */
	private LinkedList<Integer> byPeg(Peg peg) {
		return switch (peg) {
			case A -> pegA;
			case B -> pegB;
			case C -> pegC;
		};
	}

	/**
	 * @return a fresh (mutable) copy of this board.
	 */
	public HanoiBoard copy() {
		return new HanoiBoard(new LinkedList<>(pegA), new LinkedList<>(pegB), new LinkedList<>(pegC));
	}

	/**
	 * Modify a board by moving a disk.
	 * @param move from which peg to which peg a disk has to be moved
	 * @throws InvalidMoveException if this move is not allowed on the current board
	 */
	public void move(Move move) {
		var sourcePeg = byPeg(move.from());
		var targetPeg = byPeg(move.to());

		if (sourcePeg.isEmpty()) {
			throw new InvalidMoveException("Cannot move from an empty peg: " + move.from());
		}

		int diskToMove = sourcePeg.getLast();

		if (!targetPeg.isEmpty() && targetPeg.getLast() < diskToMove) {
			throw new InvalidMoveException(
					String.format("Invalid move: cannot place larger disk %d on smaller disk %d",
							diskToMove, targetPeg.getLast()));
		}

		sourcePeg.removeLast();
		targetPeg.addLast(diskToMove);
	}

	@Override
	public String toString() {
		return String.format("A:%s, B:%s, C:%s", pegA, pegB, pegC);
	}

	public String calculateBestMove(int numDisks, int source, int target, int helper) {

		if (numDisks == 1) {
			return "Bewege Plättchen " + source + " nach Turm " + target;
		}

        return calculateBestMove(numDisks - 1, source, helper, target) + "\n" +
				"Bewege Plättchen von Turm " + source + " zu Turm " + target + "\n" +
				calculateBestMove(numDisks - 1, helper, target, source);
	}


	/**
	 * Identifies one of three pegs (rods/poles/sticks).
	 */
	public enum Peg {
		A, B, C
	}

	/**
	 * A potentially valid move of a single disk, oblivious of the actual board.
	 *
	 * @param from starting peg
	 * @param to target peg
	 */
	public record Move(Peg from, Peg to) {

		public Move {
			if (from == to) {
				throw new InvalidMoveException("Cannot move to self");
			}
			if (from == null || to == null) {
				throw new InvalidMoveException("Invalid move");
			}
		}
	}
}