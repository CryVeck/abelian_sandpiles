package cryveck;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
import java.util.Scanner;

public class Tipe {

	//DÉBUT PARAMÈTRES
	public static final String PREFIXE = "avalanche";
	public static final int MAX_SIZE_LIST_IMAGE_BEFORE_PROCESS = 10;
	public static final int SAVE_IMAGE_PIXEL_SIZE = 3;
	public static final ColorProcess cp = (double param) -> Math.pow(param, 0.5);//(double param) -> Math.pow(param, 0.2) ; (double param) -> param renvoie id (i.e la fonction getColor)
	public static final boolean DEMANDE_GO = false;
	public static final int N = 60;
	//FIN PARAMÈTRES
	
	public static Rendu rendu = new Rendu(MAX_SIZE_LIST_IMAGE_BEFORE_PROCESS, SAVE_IMAGE_PIXEL_SIZE, cp);
	
	public static void main(String[] args) {
		Scanner sc;
		if (DEMANDE_GO) {
			sc = new Scanner(System.in);
			while (!sc.nextLine().equals("go"));
		}
		long t0 = System.currentTimeMillis();
		tests();
		System.out.println("Temps total d'éxecution : " + (System.currentTimeMillis()-t0));
		if (DEMANDE_GO)
			sc.close();
	}
	
	public static void tests () { // Mettre le baza là dedans
		
		//Hashtable <Integer, ArrayList<Integer>> g = Graphes.graphe3NeighbourVariante(60);
		//int configuration[] = configurationAleatoire(60*60+1,2)
		//configuration[80] = 3;
		//int lap[][] = laplacienne(g, 60*60+1);
		//stabSuivi(g, configuration, lap, 60*60+1);
		//Hashtable <Integer, ArrayList<Integer>> g = Graphes.grapheDiagonale(10, Graphes.grapheFeuille(N));
		//Hashtable <Integer, ArrayList<Integer>> gP = Graphes.grapheCercle(45, Graphes.grapheFeuille(100), 2);
		Hashtable <Integer, ArrayList<Integer>> gP = Graphes.grapheCercle(N/2-3, Graphes.grapheFeuille(N),0.5);
		int[] configuration = calculIdentiteDict(gP, false);
		//int[] configuration = configurationAleatoire(N*N+1, max);
		//stabSuivi(g, configuration, lap, N*N+1);
		//calculIdentite(g, N*N, true);
		rendu.setColorRange(3, 0);//Définition de l'intervalle pour le calcul des couleurs
		rendu.save(PREFIXE + "-" + "TESTPerform", configuration, N, N);//Comme avant sauf que l'on ne met plus l'intervalle de couleurs
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
		else
			stabSuiviDict(graphe, tmp, lap, s+1);
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
	
	public static int[] stabSuiviDict(Hashtable<Integer, ArrayList<Integer>> graphe, int[] configuration, Hashtable<Integer, Hashtable<Integer, Integer>> lap,
			int n) {
		int couleurmaximale = Integer.MIN_VALUE;
		for (int i = 0; i < configuration.length; i++)
			couleurmaximale = Math.max(couleurmaximale, configuration[i]);
		int r = 1;
		int taille = (int) Math.sqrt(n);
		rendu.setColorRange(couleurmaximale, 0);
		rendu.save(PREFIXE + "-" + String.format("%05d", 0), configuration, taille, taille);
		while (!estStableDict(lap, configuration, n)) {
			unStabDict(graphe, configuration, lap, n);
			rendu.save(PREFIXE + "-" + String.format("%05d", r++), configuration, taille, taille);
		}
		return configuration;
	}

	
	////////////////////// LAPLACIENNE DICT TEST
	
	//Pas de bonnes performances :/
	
	public static Hashtable<Long , Integer> laplacienneDictTest(Hashtable<Integer, ArrayList<Integer>> graphe, int n) {
		Hashtable<Long, Integer> M = new Hashtable<Long, Integer>(); 
		for (int i = 0; i < n; i++)
			if (graphe.containsKey(i)) {
				Long k1 = (long)i << 32 | i;
				M.put(k1, graphe.get(i).size());
				for (int j : graphe.get(i))
					if(M.get(k1) == null) {
						M.put(k1, -1);
					} else {
						long k2 = (long)i << 32 | j;
						if (M.get(k2) != null)
							M.put(k2, M.get(k2) - 1);
						else
							M.put(k2, -1);
					}
			}
		return M;
	}
	
	public static int[] calculIdentiteDictTest(Hashtable<Integer, ArrayList<Integer>> graphe, boolean suivi) {
		int s = graphe.size();
		Hashtable<Long, Integer> lap = laplacienneDictTest(graphe, s+1);
		int[] configuration = configurationCritiqueDictTest(lap, s+1);
		int[] tmp = Maths.multMatInt(configuration, 2);
		stabDictTest(graphe, tmp, lap, s+1);
		tmp = Maths.subMatMat(Maths.multMatInt(configuration, 2), tmp);
		if (!suivi)
			stabDictTest(graphe, tmp, lap, s+1);
		else
			stabSuiviDictTest(graphe, tmp, lap, s+1);
		return tmp;
	}
	
	public static int[] configurationCritiqueDictTest(Hashtable<Long , Integer> lap, int lg) {
		int[] configuration = new int[lg];
		for (int i = 1; i < lg; i++) {
			configuration[i] = lap.get((long)i << 32 | i) - 1;
		}
		return configuration;
	}
	
	public static void stabDictTest(Hashtable<Integer, ArrayList<Integer>> graphe, int[] configuration, Hashtable<Long, Integer> lap, int n) {
		while (!estStableDictTest(lap, configuration, n)) {
			unStabDictTest(graphe, configuration, lap, n);
		}
	}
	
	public static boolean estStableDictTest(Hashtable<Long, Integer> lap, int[] configuration, int n) {
		for (int i = 1; i < n; i++) {
			if (configuration[i] >= lap.get((long)i << 32 | i))
				return false;
		}
		return true;
	}
	
	public static void unStabDictTest(Hashtable<Integer, ArrayList<Integer>> graphe, int[] configuration, Hashtable<Long, Integer> lap, int n) {
		ArrayList<Integer> si = sitesInstablesDictTest(lap, configuration, n);
		for (int x : si) {
			configuration[x] -= lap.get((long)x << 32 | x);
			for (int y : graphe.get(x)) {
				Integer k = lap.get((long)x << 32 | y);
				if (k != null)
					configuration[y] -= k;
			}
		}
	}
	
	public static ArrayList<Integer> sitesInstablesDictTest(Hashtable<Long, Integer> lap, int[] configuration, int n) {
		ArrayList<Integer> J = new ArrayList<Integer>();
		for (int i = 1; i < n; i++)
			if (configuration[i] >= lap.get((long)i << 32 | i))
				J.add(i);
		return J;
	}
	
	public static int[] stabSuiviDictTest(Hashtable<Integer, ArrayList<Integer>> graphe, int[] configuration, Hashtable<Long, Integer> lap,
			int n) {
		int couleurmaximale = Integer.MIN_VALUE;
		for (int i = 0; i < configuration.length; i++)
			couleurmaximale = Math.max(couleurmaximale, configuration[i]);
		int r = 1;
		int taille = (int) Math.sqrt(n);
		rendu.setColorRange(couleurmaximale, 0);
		rendu.save(PREFIXE + "-" + String.format("%05d", 0), configuration, taille, taille);
		while (!estStableDictTest(lap, configuration, n)) {
			unStabDictTest(graphe, configuration, lap, n);
			rendu.save(PREFIXE + "-" + String.format("%05d", r++), configuration, taille, taille);
		}
		return configuration;
	}
	
}
