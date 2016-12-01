package saar.agents;

public interface Link {

	int UNDIRECTED = 0;
	int DIRECTED = 1;

	int getType();
	int getID();

	double getWeight();
	void setWeight(double Weight);

}