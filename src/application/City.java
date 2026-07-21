package application;

import java.util.Arrays;

public class City {
	private String name;
	private AdjacentCity[] adjacents;
	private int numAdjacents;

	public City(String name, int maxAdjacents) {
		this.name = name;
		this.adjacents = new AdjacentCity[maxAdjacents];
		this.numAdjacents = 0;
	}

	public AdjacentCity[] getAdjacents() {
		return Arrays.copyOf(adjacents, numAdjacents);
	}

	public String getName() {
		return name;
	}

	public void addAdjacent(AdjacentCity adjacentCity) {
		if (numAdjacents < adjacents.length) {
			adjacents[numAdjacents] = adjacentCity;
			numAdjacents++;
		} else {
			System.out.println("Cannot add more adjacent cities. Maximum limit reached.");
		}
	}

	public AdjacentCity getAdjacent(String cityName) {
		for (AdjacentCity adjacent : adjacents) {
			if (adjacent != null && adjacent.getName().equals(cityName)) {
				return adjacent;
			}
		}
		return null;
	}

	public AdjacentCity get(int index) {
		if (index >= 0 && index < numAdjacents) {
			return adjacents[index];
		} else {
			return null;
		}
	}

	public void setAdjacents(AdjacentCity[] adjacents) {
		this.adjacents = adjacents;
		this.numAdjacents = adjacents.length;
	}

	@Override
	public String toString() {
		return "City{" + "name='" + name + '\'' + '}';
	}
}
