package refactoring;

public class Board {
	private static final int BOARD_SIZE = 9;
	private static final int BLOCK_SIZE = 3;
	private static final int MIN_VALUE = 1;
	private static final int MAX_VALUE = 9;
	private static final int UNDEFINE_VALUE = 0;
	private static final int CORRECT = 9;

	private int[][] cell = new int[BOARD_SIZE][BOARD_SIZE];

	/*
	 * コンストラクタ
	 */
	public Board() {
		for (int y = 0; y < BOARD_SIZE; y++) {
			for (int x = 0; x < BOARD_SIZE; x++) {
				cell[x][y] = UNDEFINE_VALUE;
			}
		}
	}

	/*
	 * 指定したマスに数字が置けるか判定する
	 */
	public boolean canPut(int col, int row, int num) throws OutOfBoard, IllegalNumber {
		// ボードの範囲外のマスを指定していないか検証する
		if (col < 1) {
			throw new OutOfBoard();
		}
		if (col > BOARD_SIZE) {
			throw new OutOfBoard();
		}
		if (row < 1) {
			throw new OutOfBoard();
		}
		if (row > BOARD_SIZE) {
			throw new OutOfBoard();
		}

		// イレギュラーな数字を指定していないか検証する
		if (num < MIN_VALUE || num > MAX_VALUE) {
			throw new IllegalNumber();
		}

		// 座標の補正を行う
		int x = col - 1;
		int y = row - 1;

		return canPutHorizontal(y, num)
				&& canPutVertical(x, num)
				&& canPutBlock(x, y, num);
	}

	/*
	 * ブロック単位で数字が置けるか判定する
	 */
	private boolean canPutBlock(int x, int y, int num) {
		// ブロックの左上のセルを求める
		int cellLeft = (x / BLOCK_SIZE) * BLOCK_SIZE;
		int cellTop = (y / BLOCK_SIZE) * BLOCK_SIZE;

		for (x = cellLeft; x < (cellLeft + BLOCK_SIZE); x++) {
			for (y = cellTop; y < (cellTop + BLOCK_SIZE); y++) {
				if (cell[x][y] == num) {
					return false;
				}
			}
		}
		return true;
	}

	/*
	 * 縦方向で数字が置けるか判定する
	 */
	private boolean canPutVertical(int x, int num) {
		for (int y = 0; y < BOARD_SIZE; y++) {
			if (cell[x][y] == num) {
				return false;
			}
		}
		return true;
	}

	/*
	 * 横方向で数字が置けるか判定する
	 */
	private boolean canPutHorizontal(int y, int num) {
		for (int x = 0; x < BOARD_SIZE; x++) {
			if (cell[x][y] == num) {
				return false;
			}
		}
		return true;
	}

	/*
	 * 指定したマスに数字を置く
	 */
	public void put(int col, int row, int num) throws OutOfBoard, IllegalNumber {
		// ボードの範囲外のマスを指定していないか検証する
		if (col < 1 || col > BOARD_SIZE) {
			throw new OutOfBoard();
		}
		if (row < 1 || row > BOARD_SIZE) {
			throw new OutOfBoard();
		}

		// イレギュラーな数字を指定していないか検証する
		if (num < MIN_VALUE || num > MAX_VALUE) {
			throw new IllegalNumber();
		}

		// 座標の補正を行う
		int x = col - 1;
		int y = row - 1;

		cell[x][y] = num;
	}

	/*
	 * 指定したマスの数字を調べる
	 */
	public int getNumber(int col, int row) throws OutOfBoard {
		// ボードの範囲外のマスを指定していないか検証する
		if (col < 1 || col > BOARD_SIZE) {
			throw new OutOfBoard();
		}
		if (row < 1 || row > BOARD_SIZE) {
			throw new OutOfBoard();
		}

		// 座標の補正を行う
		int x = col - 1;
		int y = row - 1;

		return cell[x][y];
	}

	/*
	 * 指定したマスから数字を消す
	 */
	public void clear(int col, int row) throws OutOfBoard {
		// ボードの範囲外のマスを指定していないか検証する
		if (col < 1 || col > BOARD_SIZE) {
			throw new OutOfBoard();
		}
		if (row < 1 || row > BOARD_SIZE) {
			throw new OutOfBoard();
		}

		// 座標の補正を行う
		int x = col - 1;
		int y = row - 1;

		cell[x][y] = UNDEFINE_VALUE;
	}

	/*
	 * ゲーム盤の状態を文字列として返す
	 */
	public String toString() {
		StringBuilder s = new StringBuilder();
		for (int row = 0 ; row < BOARD_SIZE; row++) {
			if ((row % BLOCK_SIZE) == 0) {
				s.append("+---+---+---+\n");
				for (int col = 0; col < BOARD_SIZE; col++) {
					if ((col % BLOCK_SIZE) == 0) {
						s.append("|");
						s.append(toChar(col, row));
					} else {
						s.append("");
						s.append(toChar(col, row));
					}
				}
			} else {
				for (int col = 0; col < BOARD_SIZE; col++) {
					if ((col % BLOCK_SIZE) == 0) {
						s.append("|");
						s.append(toChar(col, row));
					} else {
						s.append("");
						s.append(toChar(col, row));
					}
				}
			}
			s.append("|\n");
		}
        s.append("+---+---+---+");
		return s.toString();
	}

	private char toChar(int x, int y) {
		switch (cell[x][y]) {
		case 0:
			return '.';
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
			return (char) (cell[x][y] + '0');
		default:
			return 'x';
		}
	}

	/*
	 * すべてのマスに矛盾なく数字が置いてあるか判定する
	 */
	public boolean isComplete() {
		if (isCompleteHorizontal()
				&& isCompleteVertical()
				&& isCompleteBlocks()) {
			return true;
		}
		return false;
	}

	/*
	 * 横方向に数字がもれなく配置されているか確認する
	 */
	private boolean isCompleteBlocks() {
		boolean result = true;
		for (int y = 0; y < BOARD_SIZE && result; y += BLOCK_SIZE) {
			for (int x = 0; x < BOARD_SIZE && result; x += BLOCK_SIZE) {
				int[] numbers = new int[MAX_VALUE + 1];
				for (int yy = y; yy < y + BLOCK_SIZE; yy++) {
					for (int xx = x; xx < x + BLOCK_SIZE; xx++) {
						numbers[cell[xx][yy]] = 1;
					}
				}
				if (!isComplete(numbers)) {
					result = false;
				}
			}
		}
		return result;
	}

	/*
	 * 縦方向に数字がもれなく配置されているか判定する
	 */
	private boolean isCompleteVertical() {
		boolean result = true;
		for (int x = 0; x < BOARD_SIZE && result; x++) {
			int[] numbers = new int[MAX_VALUE + 1];
			for (int y = 0; y < BOARD_SIZE; y++) {
				numbers[cell[x][y]] = 1;
			}
			if (!isComplete(numbers)) {
				result = false;
			}
		}
		return result;
	}

	/*
	 * 横方向に数字がもれなく配置されているか判定する
	 */
	private boolean isCompleteHorizontal() {
		boolean result = true;
		for (int y = 0; y < BOARD_SIZE && result; y++) {
			int[] numbers = new int[MAX_VALUE + 1];
			for (int x = 0; x < BOARD_SIZE; x++) {
				numbers[cell[x][y]] = 1;
			}
			if (!isComplete(numbers)) {
				result = false;
			}
		}
		return result;
	}

	/*
	 * 配列の中に、1から9までの数字が一つずつある場合はtrueを返す
	 */
	private boolean isComplete(int[] numbers) {
		int answer = 0;
		for (int number : numbers) {
			answer += number;
		}
		if (answer != CORRECT) {
			return false;
		}
		return true;
	}
}

@SuppressWarnings("serial")
class OutOfBoard extends Exception {
}

@SuppressWarnings("serial")
class IllegalNumber extends Exception {
}
