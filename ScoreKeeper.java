import java.util.Scanner;
import java.lang.Math;
import java.io.File;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Arrays;
import java.util.Comparator;
public class ScoreKeeper{
	Scanner scan = new Scanner(System.in);
	int rounds;
	int people;
	String[] players;
	int crown = -1;
	int[] bets;
	int[] results;
	int[] scores;
	File file;
	PrintWriter output;
	public static void main(String[] args){
		//System.out.println("Play! Jungle Bridge \nEnter: \treset \t\tto reset the round,\n\tprevious \tto redo the previous round,\n\texit \t\tto restart the game,");
		ScoreKeeper sk = new ScoreKeeper();

		System.out.println("\nEnter any key to exit.");
		Scanner scan1 = new Scanner(System.in);
		scan1.next();
	}

	private void createFile(){
		String name = "Jungle Bridge - ";
		for(int i = 0; i < people - 1; i++){
			name+= players[i] + ", ";
		}
		name += players[people - 1] + ".txt";
		file = new File(name);
		
		try{
			output = new PrintWriter(file);
		} catch(Exception a){
			createFile();
		}

		String row1 = "Round\t\t";
		for(int i = 0; i < people; i++){
			row1 += players[i] + "\t\t";
		}
					DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			String datetime = dateFormat.format(date); //2016/11/16 12:08:43
		output.println("Scoreboard: (bet - actual: score)" + "\t"+datetime);
		output.println("-----------------------------------------------------------");
		output.println(row1);
		output.println("-----------------------------------------------------------");
	}

	public ScoreKeeper(){
		getPeople();
		getRounds();
		players();
		scores = new int[people];
		createFile();
		play();
		winner();
		//finalScores();
		output.close();
	}
	//find way to change entered data in case of error.
	private void finalScores(){
		//print out final scores to text file.
	}
	private void getRounds(){
		System.out.print("How Many Rounds? ");
		try{
			String clear = scan.nextLine();
			rounds = Integer.parseInt(clear);
			if(rounds < 1 || rounds > 13){
				throw new Exception();
			}
		} catch (Exception a){
			System.out.println("\nError, enter an number between 1 and 13 inclusive.");
			getRounds();
		}
	}
	//figure out how many rounds are possible for each player number.
	private void getPeople(){
		System.out.print("How Many People? ");
		try{
			String clear = scan.nextLine();
			people = Integer.parseInt(clear);
			if(people < 2 || people > 7){
				throw new Exception();
			}
		} catch (Exception a){
			System.out.println("\nError, enter an integer between 2 and 7 inclusive.");
			getPeople();
		}
	}
	private void players(){
		players = new String[people];
		System.out.println("\nName The Players:");
		for(int i = 0; i < people; i++){
			System.out.print("Player " + (i + 1) + ":\t");
			players[i] = scan.nextLine();
		}
		System.out.println("------------------------------------");
	}
	private void crown(){
		if(crown < 0) {
			crown = (int)(Math.random()*people);
		} else {
			crown++;
			crown = crown%people;
		}
	}
	private void play(){
		int levels = 2 * rounds - 1;
		for(int i = 0; i < levels; i++){
			playRound(i);
		}
	}
	public int[] argsort(final int[] a, final boolean ascending) {
        Integer[] indexes = new Integer[a.length];
        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = i;
        }
        Arrays.sort(indexes, new Comparator<Integer>() {
            @Override
            public int compare(final Integer i1, final Integer i2) {
                return (ascending ? 1 : -1) * Integer.compare(a[i1], a[i2]);
            }
        });
        int[] b = new int[indexes.length];
        for (int i = 0; i < b.length; i++) {
            b[i] = indexes[i].intValue();
        }
        return b;
    }
	private void playRound(int level){
		crown();
		System.out.println("Round " + roundnum(level) + ": " + players[crown] + " is the Crown.\n");
		getBets(level);
		getResults(level);
		calcScores();
		System.out.println("Scores:");
		//for round with highest number, say that its the wild round, no jungle suit.
		//display scores from highest to lowest.
		int[] b;
		b = this.argsort(scores, false);
		for(int i = 0; i < people; i++){
			System.out.println(players[b[i]] + ":\t" + scores[b[i]]);
		}
		scoreboard(level);
		System.out.println("------------------------------------");
	}
	public void scoreboard(int level){
		String row = "" + roundnum(level) + "\t";
		for(int i = 0; i < people; i++){
			row += "\t" + bets[i] + " - " +results[i] + ": " + scores[i]; 
		}
		output.println(row);
	}

	int betSum = 0;
	int resSum = 0;
	private void getResults(int level){
		results = new int[people];
		resSum = 0;
		System.out.println("Tricks Won:");
		for(int i = 0; i < people; i++){
			getResult(i, level);
		}
		System.out.println();
	}
	private void getResult(int index, int level){
		int result;
		try{
			System.out.print(players[(crown + index)%people] + ":\t");
			String clear = scan.nextLine();
			result = Integer.parseInt(clear);
			resSum += result;
			if(result < 0 || result > roundnum(level)){
				System.out.print("Error, ");
				resSum -= result;
				throw new Exception();
			}
			if(resSum > roundnum(level)){
				System.out.print("Error, tricks won should sum to the number of the round.\n");
				resSum -= result;
				throw new Exception();
			}
			if(index == people - 1 && resSum != roundnum(level)){
				System.out.print("Error, tricks won should sum to the number of the round.\n");
				resSum -= result;
				throw new Exception();
			}
			results[(crown + index)%people] = result;
		} catch (Exception a){
			System.out.println("Enter a number between 0 and " + roundnum(level) + " inclusive.");
			getResult(index, level);
		}
	}
	private void getBets(int level){
		bets = new int[people];
		betSum = 0;
		System.out.println("Place Your Bets:");
		for(int i = 0; i < people; i++){
			getBet(i, level);
		}
		System.out.println();
	}
	private void getBet(int index, int level){
		int bet;
		try{
			System.out.print(players[(crown + index)%people] + ":\t");
			String clear = scan.nextLine();
			bet = Integer.parseInt(clear);
			betSum += bet;
			if(bet < 0 || bet > roundnum(level)){
				System.out.print("Error, ");
				betSum -= bet;
				throw new Exception();
			}
			if(roundnum(level) > 3 && index == people - 1 && betSum == roundnum(level)){
				System.out.print("Error, bets cannot sum to the number of the round.\n");
				betSum -= bet;
				throw new Exception();
			}
			bets[(crown + index)%people] = bet;
		} catch (Exception a){
			//if invalid number number entered, clarify bottom statement to not include entered number.
			System.out.println("Enter a number between 0 and " + roundnum(level) + " inclusive.");
			getBet(index, level);
		}
	}
	private void calcScores(){
		for(int i = 0; i < people; i++){
			int add = 0;
			add = results[i] - Math.abs((results[i] - bets[i]));
			if(bets[i] == results[i]){
				add += 10;
			}
			scores[i] += add;
		}
	}
	private int roundnum(int level){
		if(level < rounds) {
			return level%rounds + 1;
		} else if(level == rounds){
			return rounds - 1;
		} else {
			return (-1*level-1)%rounds + rounds;
		}
	}
	//wont work for a tie.
	private void winner(){
		int index = 0;
		for(int i = 0; i < people; i++){
			if(scores[i] > scores[index]){
				index = i;
			}
		}
		System.out.println(players[index] + " is the winner.");
		output.println("");
		output.println(players[index] + " is the winner.");
	}
}