package cryveck;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
import java.util.Scanner;
public class Tipe {

	public static final String prefixe = "avalanche";
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		while (!sc.nextLine().equals("go"));
		long t0 = System.currentTimeMillis();
		int n = 400;
		//Hashtable <Integer, ArrayList<Integer>> g = Graphes.graphe3NeighbourVariante(60);
		//int configuration[] = configurationAleatoire(60*60+1,2);
		//configuration[80] = 3;
		//int lap[][] = laplacienne(g, 60*60+1);
		//stabSuivi(g, configuration, lap, 60*60+1);
		//Hashtable <Integer, ArrayList<Integer>> g = Graphes.grapheDiagonale(10, Graphes.grapheFeuille(n));
		//Hashtable <Integer, ArrayList<Integer>> gP = Graphes.grapheCercle(45, Graphes.grapheFeuille(100), 2);
		Hashtable <Integer, ArrayList<Integer>> gP = Graphes.grapheCercle(n/2-3, Graphes.grapheFeuille(n),0.5);
		int[] configuration = calculIdentiteDict(gP, false);
		Rendu.save(prefixe + "-" + "TEST", configuration, n, n, 3);
		//int[] configuration = configurationAleatoire(n*n+1, max);
		//stabSuivi(g, configuration, lap, n*n+1);
		//calculIdentite(g, n*n, true);
		System.out.println(System.currentTimeMillis()-t0);
		sc.close();
	}
	
	public static int[] calculAire(int[] configuration) {
		int[] aire = new int[Maths.max(configuration)+1];
		for (int i = 1; i<configuration.length; i++) {
			aire[configuration[i]] += 1;
		}
		return aire;
	}

	public static int[] configurationAleatoire(int n, int max) {
		int[] result = new int[n];
		Random rand = new Random();
		for (int i = 0; i < n; i++)
			result[i] = rand.nextInt(max + 1);
		return result;
	}

	public static int[] configurationCritique(short[][] lap) {
		int lg = lap[0].length;
		int[] configuration = new int[lg];
		for (int i = 1; i < lg; i++)
			configuration[i] = lap[i][i] - 1;
		return configuration;
	}
	
	public static int[] configurationComplete(int lg, int C) {
		int[] configuration = new int[lg +1];
		for (int i = 1; i<lg+1; i++)
			configuration[i] = C;
		return configuration;
	}
	
	public static int[] configurationMilieu(int n, int h) {
		int[] l = new int[n * n + 1];
		l[(int) (n * n / 2) + 1] = h;
		return l;
	}
	
	/////////////////////////// LAPLACIENNE DICT
	
	
	public static Hashtable<Integer, Hashtable<Integer, Integer>> laplacienneDict(Hashtable<Integer, ArrayList<Integer>> graphe, int n) {
		Hashtable<Integer, Hashtable<Integer, Integer>> M = new Hashtable<Integer, Hashtable<Integer, Integer>>(); 
		for (int i = 0; i < n; i++)
			if (graphe.containsKey(i)) {
				if(M.get(i) == null)
					M.put(i, new Hashtable<Integer, Integer>());
				M.get(i).put(i, graphe.get(i).size());
				for (int j : graphe.get(i))
					if(M.get(i) == null) {
						M.put(i, new Hashtable<Integer, Integer>());
						M.get(i).put(i, -1);
					} else {
						if (M.get(i).containsKey(j))
							M.get(i).put(j, M.get(i).get(j)-1);
						else
							M.get(i).put(j, -1);
					}
			}
		return M;
	}
	
	public static int[] calculIdentiteDict(Hashtable<Integer, ArrayList<Integer>> graphe, boolean suivi) {
		int s = graphe.size();
		Hashtable<Integer, Hashtable<Integer, Integer>> lap = laplacienneDict(graphe, s+1);
		int[] configuration = configurationCritiqueDict(lap, s+1);
		int[] tmp = Maths.multMatInt(configuration, 2);
		stabDict(graphe, tmp, lap, s+1);
		tmp = Maths.subMatMat(Maths.multMatInt(configuration, 2), tmp);
		if (!suivi)
			stabDict(graphe, tmp, lap, s+1);
//		else
//			stabSuivi(graphe, tmp, lap, s+1);
		return tmp;
	}
	
	public static int[] configurationCritiqueDict(Hashtable<Integer, Hashtable<Integer, Integer>> lap, int lg) {
		int[] configuration = new int[lg];
		for (int i = 1; i < lg; i++)
			if (lap.get(i) != null)
				configuration[i] = lap.get(i).get(i) - 1;
			else
				configuration[i] = -1;
		return configuration;
	}
	
	public static void stabDict(Hashtable<Integer, ArrayList<Integer>> graphe, int[] configuration, Hashtable<Integer, Hashtable<Integer, Integer>> lap, int n) {
		while (!estStableDict(lap, configuration, n)) {
			unStabDict(graphe, configuration, lap, n);
		}
	}
	
	public static boolean estStableDict(Hashtable<Integer, Hashtable<Integer, Integer>> lap, int[] configuration, int n) {
		for (int i = 1; i < n; i++)
			if (lap.get(i) != null) {
				if (configuration[i] >= lap.get(i).get(i))
					return false;
			} else
				if (configuration[i] >= 0)
					return false;
		return true;
	}
	
	public static void unStabDict(Hashtable<Integer, ArrayList<Integer>> graphe, int[] configuration, Hashtable<Integer, Hashtable<Integer, Integer>> lap, int n) {
		ArrayList<Integer> si = sitesInstablesDict(lap, configuration, n);
		for (int x : si) {
			if (lap.get(x) != null) {
				configuration[x] -= lap.get(x).get(x);
				for (int y : graphe.get(x))
					configuration[y] -= lap.get(x).get(y);
			}
		}
	}
	
	public static ArrayList<Integer> sitesInstablesDict(Hashtable<Integer, Hashtable<Integer, Integer>> lap, int[] configuration, int n) {
		ArrayList<Integer> J = new ArrayList<Integer>();
		for (int i = 1; i < n; i++)
			if (lap.get(i) != null) {
				if (configuration[i] >= lap.get(i).get(i))
					J.add(i);
			} else {
				if (configuration[i] >= 0)
					J.add(i);
			}
			
		return J;
	}

}
