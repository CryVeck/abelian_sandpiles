package cryveck;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
import java.util.Set;
public class Tipe {

	public static final String prefixe = "A";
	public static void main(String[] args) {
		long t0 = System.currentTimeMillis();
		int n = 10;
		Hashtable <Integer, ArrayList<Integer>> g = Graphes.graphe3Neighbour(n);
		System.out.println(g);
		int[] configuration = configurationComplete(n*n, 10);
		int lap[][]  = laplacienne(g, n*n+1);
		stabSuivi(g, configuration, lap, n*n+1);
		System.out.println(System.currentTimeMillis() - t0);
	}
	
	public static int[][] laplacienne(Hashtable<Integer, ArrayList<Integer>> graphe, int n) {
		int[][] M = new int[n][n];
		for (int i = 0; i < n; i++)
			if (graphe.containsKey(i)) {
				M[i][i] = graphe.get(i).size();
				for (int j : graphe.get(i))
					M[i][j] -= 1;
			}
		return M;
	}

	public static ArrayList<Integer> sitesInstables(int[][] lap, int[] configuration, int n) {
		ArrayList<Integer> J = new ArrayList<Integer>();
		for (int i = 1; i < n; i++)
			if (configuration[i] >= lap[i][i])
				J.add(i);
		return J;
	}

	public static void topple(Hashtable<Integer, ArrayList<Integer>> graphe, int[][] lap, int[] configuration, int n,
			int x) {
		Set<Integer> keys = graphe.keySet();
		for (int y : keys)
			configuration[y] -= lap[x][y];
	}

	public static void unStab(Hashtable<Integer, ArrayList<Integer>> graphe, int[] configuration, int[][] lap, int n) {
		ArrayList<Integer> si = sitesInstables(lap, configuration, n);
		for (int j : si)
			topple(graphe, lap, configuration, n, j);
	}

	public static boolean estStable(int[][] lap, int[] configuration, int n) {
		for (int i = 1; i < n; i++)
			if (configuration[i] >= lap[i][i])
				return false;
		return true;
	}

	public static void stab(Hashtable<Integer, ArrayList<Integer>> graphe, int[] configuration, int[][] lap, int n) {
		while (!estStable(lap, configuration, n)) {
			unStab(graphe, configuration, lap, n);
		}
	}

	public static int[] calculIdentite(Hashtable<Integer, ArrayList<Integer>> graphe, int lg, boolean suivi) {
		int[][] lap = laplacienne(graphe, lg + 1);
		int[] configuration = new int[lg + 1];
		for (int i = 1; i < lg + 1; i++) {
			configuration[i] = lap[i][i] - 1;
		}
		int[] tmp = Maths.multMatInt(configuration, 2);
		stab(graphe, tmp, lap, lg + 1);
		tmp = Maths.subMatMat(Maths.multMatInt(configuration, 2), tmp);
		if (!suivi)
			stab(graphe, tmp, lap, lg + 1);
		else
			stabSuivi(graphe, tmp, lap, lg + 1);
		return tmp;
	}

	public static int[] calculIdentite(Hashtable<Integer, ArrayList<Integer>> graphe, int lg) {
		return calculIdentite(graphe, lg, false);
	}

	public static int[] configurationAleatoire(int n) {
		int[] result = new int[n];
		Random rand = new Random();
		for (int i = 0; i < n; i++)
			result[i] = rand.nextInt(n + 1);
		return result;
	}

	public static int[] configurationCritique(int[][] lap, int lg) {
		int[] configuration = new int[lg + 1];
		for (int i = 1; i < lg + 1; i++)
			configuration[i] = lap[i][i] - 1;
		return configuration;
	}
	
	public static int[] configurationComplete(int lg, int C) {
		int[] configuration = new int[lg +1];
		for (int i = 1; i<lg+1; i++)
			configuration[i] = C;
		return configuration;
	}
	public static int[] stabSuivi(Hashtable<Integer, ArrayList<Integer>> graphe, int[] configuration, int[][] lap,
			int n) {
		int couleurmaximale = Integer.MIN_VALUE;
		for (int i = 0; i < configuration.length; i++)
			couleurmaximale = Math.max(couleurmaximale, configuration[i]);
		int r = 1;
		int taille = (int) Math.sqrt(n);
		Rendu.save(prefixe + "-" + String.format("%05d", 0), configuration, taille, taille, couleurmaximale);
		while (!estStable(lap, configuration, n)) {
			unStab(graphe, configuration, lap, n);
			Rendu.save(prefixe + "-" + String.format("%05d", r++), configuration, taille, taille, couleurmaximale);
		}
		return configuration;
	}

	public static int[] configurationMilieu(int n, int h) {
		int[] l = new int[n * n + 1];
		l[(int) (n * n / 2) + 1] = h;
		return l;
	}
}
