package application;

import java.io.*;
import java.util.*;

public class PathFinder {
	private City[] cities;
	private int numCities;
	private String startCity;
	private String endCity;
	private int[][] dp;
	private int[][] path;

	public void loadFromFile(File file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));

		numCities = Integer.parseInt(br.readLine().trim());
		cities = new City[numCities];

		String[] startEnd = br.readLine().split(", ");
		startCity = startEnd[0];
		endCity = startEnd[1];

		String line;
		int index = 0;
		Set<String> cityNames = new HashSet<>();

		while ((line = br.readLine()) != null) {
			String[] parts = line.split(", ");
			String cityName = parts[0];

			if (cityName.equalsIgnoreCase("Str"))
				continue;

			City existingCity = findCityByName(cityName);
			if (existingCity == null) {
				City city = new City(cityName, numCities);
				for (int i = 1; i < parts.length; i++) {
					String[] adj = parts[i].substring(1, parts[i].length() - 1).split(",");
					city.addAdjacent(new AdjacentCity(adj[0].trim(), Integer.parseInt(adj[1].trim()),
							Integer.parseInt(adj[2].trim())));
				}
				cities[index++] = city;
				cityNames.add(cityName);
			} else {
				for (int i = 1; i < parts.length; i++) {
					String[] adj = parts[i].substring(1, parts[i].length() - 1).split(",");
					existingCity.addAdjacent(new AdjacentCity(adj[0].trim(), Integer.parseInt(adj[1].trim()),
							Integer.parseInt(adj[2].trim())));
				}
			}
		}

		if (findCityByName(endCity) == null && index < numCities) {
			cities[index] = new City(endCity, numCities);
		}
	}

	public void calculateDP() {
		dp = new int[numCities + 1][numCities + 1];
		path = new int[numCities + 1][numCities + 1];

		for (int i = 0; i < dp.length; i++) {
			for (int j = 0; j < dp.length; j++) {
				if (i == j) {
					dp[i][j] = 0;
				} else {
					dp[i][j] = Integer.MAX_VALUE;
				}
			}
		}

		for (int i = 0; i < cities.length; i++) {
			City city = cities[i];
			if (city != null) {
				for (AdjacentCity adjCity : city.getAdjacents()) {
					int cityIndex = getIndexByName(city.getName());
					int adjCityIndex = getIndexByName(adjCity.getName());
					if (cityIndex != -1 && adjCityIndex != -1) {
						dp[cityIndex][adjCityIndex] = adjCity.getPetrolCost() + adjCity.getHotelCost();
					}
				}
			}
		}

		for (int k = 0; k < dp.length; k++) {
			for (int i = 0; i < dp.length; i++) {
				for (int j = 0; j < dp.length; j++) {
					if (dp[i][k] != Integer.MAX_VALUE && dp[k][j] != Integer.MAX_VALUE
							&& dp[i][k] + dp[k][j] < dp[i][j]) {
						dp[i][j] = dp[i][k] + dp[k][j];
						path[i][j] = k;
					}
				}
			}
		}
	}

	public String getBestPath() {
		return getPath(path, startCity, endCity);
	}

	public int getCost() {
		return calculatePathCost(getBestPath());
	}

	public String getAllAlternativePaths() {
		Route[] allPaths = getAllPathsWithCost(startCity, new String[0]);
		Arrays.sort(allPaths, Comparator.comparingInt(Route::getCost));

		StringBuilder sb = new StringBuilder();
		for (Route r : allPaths) {
			sb.append(r.getPath()).append(" (Cost: ").append(r.getCost()).append(")\n");
		}
		return sb.toString();
	}

	public String getDPTable() {
		return generateTableString(dp);
	}

	public String getPathTable() {
		return generateTableString(path);
	}

	private int calculatePathCost(String pathStr) {
		String[] nodes = pathStr.split(" -> ");
		int cost = 0;
		for (int i = 0; i < nodes.length - 1; i++) {
			City c = findCityByName(nodes[i]);
			if (c != null) {
				AdjacentCity adj = c.getAdjacent(nodes[i + 1]);
				if (adj != null)
					cost += adj.getHotelCost() + adj.getPetrolCost();
			}
		}
		return cost;
	}

	private City findCityByName(String name) {
		for (City c : cities) {
			if (c != null && c.getName().equals(name))
				return c;
		}
		return null;
	}

	private int getIndexByName(String name) {
		for (int i = 0; i < cities.length; i++) {
			if (cities[i] != null && cities[i].getName().equals(name))
				return i;
		}
		return -1;
	}

	private String getPath(int[][] path, String start, String end) {
		int i = getIndexByName(start);
		int j = getIndexByName(end);

		if (i == j)
			return cities[i].getName();
		int k = path[i][j];
		if (k == 0)
			return cities[i].getName() + " -> " + cities[j].getName();

		String p1 = getPath(path, start, cities[k].getName());
		String p2 = getPath(path, cities[k].getName(), end);

		if (p1.endsWith(cities[k].getName()))
			p1 = p1.substring(0, p1.lastIndexOf(cities[k].getName()));

		return p1 + p2;
	}

	private String generateTableString(int[][] table) {
		int startIdx = getIndexByName(startCity);
		int endIdx = getIndexByName(endCity);

		StringBuilder sb = new StringBuilder();
		sb.append("\t");
		for (int j = 0; j < cities.length; j++) {
			if (cities[j] != null)
				sb.append(cities[j].getName()).append("\t");
		}
		sb.append("\n");

		for (int i = 0; i < cities.length; i++) {
			if (cities[i] == null)
				continue;
			sb.append(cities[i].getName()).append("\t");
			for (int j = 0; j < cities.length; j++) {
				if (cities[j] == null)
					continue;
				if (table[i][j] == Integer.MAX_VALUE)
					sb.append("INF\t");
				else
					sb.append(table[i][j]).append("\t");
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	private Route[] getAllPathsWithCost(String currentCity, String[] pathSoFar) {
		String[] newPath = Arrays.copyOf(pathSoFar, pathSoFar.length + 1);
		newPath[pathSoFar.length] = currentCity;

		if (currentCity.equals(endCity)) {
			int cost = calculatePathCost(String.join(" -> ", newPath));
			return new Route[] { new Route(String.join(" -> ", newPath), cost) };
		}

		List<Route> allPaths = new ArrayList<>();
		City city = findCityByName(currentCity);
		if (city == null)
			return new Route[0];

		for (AdjacentCity adj : city.getAdjacents()) {
			if (!Arrays.asList(newPath).contains(adj.getName())) {
				Route[] recPaths = getAllPathsWithCost(adj.getName(), newPath);
				allPaths.addAll(Arrays.asList(recPaths));
			}
		}
		return allPaths.toArray(new Route[0]);
	}
}
