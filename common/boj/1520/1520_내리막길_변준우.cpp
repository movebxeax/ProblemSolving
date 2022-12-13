#include <iostream>
#include <queue>

int grid[2][500][500];
int M, N;
int ans = 0;
const int directions[4][2] = { {-1, 0 }, {1, 0}, {0, -1}, {0, 1} };

typedef struct Pos {
	int x, y;
	Pos(int x, int y) : x(x), y(y) {}
	Pos() : x(0), y(0) {}

	bool operator<(const Pos& p) const {
		return grid[0][this->y][this->x] < grid[0][p.y][p.x];
	}
} Pos;

bool isAvailable(int x, int y)
{
	return x >= 0 && x < N&& y >= 0 && y < M;
}

int bfs()
{
	grid[1][0][0] = 1;
	std::priority_queue<Pos> pq;
	pq.emplace(0, 0);

	while (!pq.empty())
	{
		Pos p = pq.top();
		pq.pop();

		for (const auto &itr : directions)
		{
			int ny = p.y + itr[0];
			int nx = p.x + itr[1];

			if (isAvailable(nx, ny) && grid[0][ny][nx] < grid[0][p.y][p.x])
			{
				if (grid[1][ny][nx] == 0)
				{
					grid[1][ny][nx] = grid[1][p.y][p.x];
					pq.emplace(nx, ny);
				}
				else
					grid[1][ny][nx] += grid[1][p.y][p.x];
			}
		}
	}
	
	return grid[1][M - 1][N - 1];
}

int main()
{
	std::ios::sync_with_stdio(false); std::cin.tie(0); std::cout.tie(0);

	std::cin >> M >> N;

	for (auto i = 0; i < M; i++)
		for (auto j = 0; j < N; j++)
			std::cin >> grid[0][i][j];

	std::cout << bfs();
}