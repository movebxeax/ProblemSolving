import java.util.ArrayList;
import java.util.Arrays;

public class Puzzle {

	static int boardSize;

	static int[][][] tables;

	static int[][] direction = {{0,-1}, {0,1}, {-1, 0}, {1, 0}}; // l r u d
	static int[][] turns = {{0,1,2,3}, {3,2,0,1}, {2,3,1,0}, {1,0,3,2}}; // 0, -90, 90, 180
	static boolean[][] isVisited;

	static ArrayList<EmptyPos> empty_positions;

	static class EmptyPos{
		int startX, startY;
		ArrayList<Integer> directions;
		int size;
		boolean isFilled;
		public EmptyPos(int startX, int startY, ArrayList<Integer> directions) {
			this.startX = startX;
			this.startY = startY;
			this.directions = directions;
			this.isFilled = false;
			this.size = 0;
		}
	}

	static class Chunk{
		ArrayList<Integer>[] directions;
		int size;
		boolean isUsed;
		int startX, startY;
		public Chunk(int startX, int startY, ArrayList<Integer>[] directions) {
			this.directions = directions;
			this.startX = startX;
			this.startY = startY;
		}
	}

	static void dump(int[][] target)
	{
		for(int j=0;j<boardSize; j++)
		{
			for(int i=0;i<boardSize; i++)
			{
				System.out.print(target[j][i]);
			}
			System.out.println();
		}
	}

	static void turn_table(int[][] table)
	{
		// right90deg
		for(int j=0;j<boardSize;j++)
			for(int i=0;i<boardSize;i++)
				tables[1][i][boardSize-j-1] = table[j][i];

		// left90deg
		for(int j=0;j<boardSize;j++)
			for(int i=0;i<boardSize;i++)
				tables[2][boardSize-i-1][j] = table[j][i];

		// 180deg
		for(int j=0;j<boardSize;j++)
			for(int i=0;i<boardSize;i++)
				tables[3][boardSize-j-1][boardSize-i-1] = table[j][i];
	}

	static boolean isAvailable(int x, int y)
	{
		return (x>=0 && x<boardSize && y>=0 && y < boardSize);
	}

	static int visit(int[][] game_board, int startY, int startX, ArrayList<Integer> list, int comp)
	{
		if(list==null)
			list = new ArrayList<>();
		int size = 1;
		for(int i=0;i<4;i++)
		{
			int newY = startY + direction[i][0];
			int newX = startX + direction[i][1];

			if(isAvailable(newX, newY))
			{
				if(game_board[newY][newX] != comp)
					continue;

				if(isVisited[newY][newX])
					continue;

				isVisited[newY][newX] = true;
				list.add(i);
				size += visit(game_board, newY, newX, list, comp);
			}
		}

		return size;
	}

	static ArrayList<Chunk> make_chunk_list(int[][] table)
	{
		ArrayList<Chunk> result = new ArrayList<Chunk>();

		int type = 1;

		for(int i=0;i<boardSize;i++)
			Arrays.fill(isVisited[i], false);

		for(int j=0;j<boardSize;j++)
		{
			for(int i=0;i<boardSize;i++)
			{
				if(table[j][i] == 1)
				{
					if(!isVisited[j][i])
					{
						isVisited[j][i] = true;
						Chunk c = new Chunk(i, j, new ArrayList[4]);

						for(int k=0;k<4;k++)
							c.directions[k] = new ArrayList<>();

						c.size = visit(table, j, i, c.directions[0], 1);

						for(int k=1;k<4;k++)
						{
							// can't mod with iterator. use index!!
							//for(Integer itr : c.directions[0])
							for(int n=0; n<c.directions[0].size();n++)
							{
								c.directions[k].add(turns[k][c.directions[0].get(n)]); 
							}
						}

						result.add(c);
					}
				}
			}
		}

		return result;
	}

	static ArrayList<EmptyPos> get_empty_pos(int[][] game_board)
	{
		ArrayList<EmptyPos> positions = new ArrayList<>();

		for(int i=0;i<boardSize;i++)
			Arrays.fill(isVisited[i], false);

		for(int j=0;j<boardSize;j++)
		{
			for(int i=0;i<boardSize;i++)
			{
				if(game_board[j][i] == 0)
				{
					if(!isVisited[j][i])
					{
						isVisited[j][i] = true;
						EmptyPos ep = new EmptyPos(i,j, new ArrayList<Integer>());
						ep.size = visit(game_board, j, i, ep.directions, 0);

						positions.add(ep);
					}
				}
			}
		}
		return positions;
	}

	static boolean isSameList(ArrayList<Integer> a, ArrayList<Integer> b)
	{
		boolean result = true;
		int sizeA = a.size();
		int sizeB = b.size();

		if(sizeA != sizeB)
			result = false;
		else
		{
			for(int i=0;i<sizeA;i++)
			{
				if(a.get(i) != b.get(i))
				{
					result = false;
					break;
				}
			}
		}

		return result;
	}

	public static int solution(int[][] game_board, int[][] table) {
		int answer = 0;

		// get board size and create isVisited grid with it
		boardSize = game_board.length;
		isVisited = new boolean[boardSize][boardSize];

		// get every empty pos in game_board
		empty_positions = get_empty_pos(game_board);

		// get every chunk in table
		ArrayList<Chunk> table_chunk_list = make_chunk_list(table);

		// check positions
		for(EmptyPos itr : empty_positions)
		{
			for(Chunk chunk : table_chunk_list)
			{
				if(itr.isFilled || chunk.isUsed)
					continue;

				for(ArrayList<Integer> directions_itr : chunk.directions)
				{
					if(isSameList(itr.directions, directions_itr))
					{
						itr.isFilled = true;
						chunk.isUsed = true;
						answer += itr.size;
						break;
					}
				}

			}
		}

		System.out.println(answer);

		return answer;
	}

	public static void main(String[] args) {
		int[][] game_board = {{1,1,0,0,1,0},{0,0,1,0,1,0},{0,1,1,0,0,1},{1,1,0,1,1,1},{1,0,0,0,1,0},{0,1,1,1,0,0}};
		int[][] table = {{1,0,0,1,1,0},{1,0,1,0,1,0},{0,1,1,0,1,1},{0,0,1,0,0,0},{1,1,0,1,1,0},{0,1,0,0,0,0}};

		//turn check table
		//int[][] table = {{1,2,3,4,5,6},{2,3,4,5,6,7},{3,4,5,6,7,8},{4,5,6,7,8,9},{5,6,7,8,9,0},{6,7,8,9,0,1}};
		solution(game_board, table);
	}

}
