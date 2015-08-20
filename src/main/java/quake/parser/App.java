package quake.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import quake.parser.models.Game;

public class App {

	private static final String WORLD = "<world>";

	public static void main( String[] args ) throws IOException {

		List<Game> games = parse( "games.log" );

		createLogParser( games );

		report( games );

		meansOfDeath( games );
	}

	private static List<Game> parse( String fileName ) throws IOException {
		// Ler o arquivo de log
		BufferedReader br = new BufferedReader( new FileReader( fileName ) );

		/* Regex para verificar o conteúdo inicial de cada linha */
		Pattern pClient = Pattern.compile( "\\s*\\d{1,2}:\\d{2}\\s*(ClientUserinfoChanged)\\w*" );
		Matcher mClient = pClient.matcher( "" );

		Pattern pInitGame = Pattern.compile( "\\s*\\d{1,2}:\\d{2}\\s*(InitGame)\\w*" );
		Matcher mInitGame = pInitGame.matcher( "" );

		Pattern pEndGame = Pattern.compile( "\\s*\\d{1,2}:\\d{2}\\s*(ShutdownGame)\\w*" );
		Matcher mEndGame = pEndGame.matcher( "" );

		Pattern pKill = Pattern.compile( "\\s*\\d{1,2}:\\d{2}\\s*(Kill)\\w*" );
		Matcher mKill = pKill.matcher( "" );

		int count = 0;
		String line;
		Game game = null;
		Map<String, Integer> killsForPlayers = null;
		Map<String, Integer> meansOfDeath = null;
		Integer totalKills = null;
		List<Game> games = new ArrayList<Game>();
		try {
			/* Interage por cada linha do log */
			while ( ( line = br.readLine() ) != null ) {
				line = line.trim();
				mKill.reset( line );// Passa a linha para o regex verificar
				mInitGame.reset( line );
				mEndGame.reset( line );
				mClient.reset( line );

				if ( mInitGame.find() ) {
					/*
					 * Regex verificou que inicia um game
					 */
					count++;
					if ( game != null && !games.contains( game ) ) {// Pega o
																	// game que
																	// nao
																	// aparece
																	// no log
																	// como
																	// terminado
						game.setTotalKills( totalKills );
						game.setKillsForPlayers( killsForPlayers );
						game.setMeansOfDeath( meansOfDeath );
						games.add( game );
					}
					game = new Game();
					totalKills = 0;
					killsForPlayers = new HashMap<String, Integer>();
					meansOfDeath = new HashMap<String, Integer>();
					game.setName( "game_" + count );
				} else if ( mClient.find() ) {
					/*
					 * Regex verificou que um player entrou
					 */
					// Quebra a linha em partes
					String[] split = line.split( "\\\\" );
					// Adiciona o novo player com kills = 0
					if ( killsForPlayers.get( split[1] ) == null )
						killsForPlayers.put( split[1], 0 );
				} else if ( mKill.find() ) {
					/*
					 * Regex verificou que houve um kill
					 */
					totalKills++;
					String[] split = line.split( "killed" );
					String[] splitFirstPlayer = split[0].split( ":" );
					String[] splitSecondPlayer = split[1].split( "by" );
					String firstPlayer = splitFirstPlayer[splitFirstPlayer.length - 1].trim();
					String secondPlayer = splitSecondPlayer[0].trim();
					if ( firstPlayer.equalsIgnoreCase( WORLD ) )
						/* Remove um kill do player morto pelo WORLD */
						killsForPlayers.put( secondPlayer, killsForPlayers.get( secondPlayer ).intValue() - 1 );
					else {
						/* Adiciona um kill ao player que matou o outro player */
						killsForPlayers.put( firstPlayer, killsForPlayers.get( firstPlayer ).intValue() + 1 );
					}

					// Verifica se a causa da morte já foi adicionada
					if ( !meansOfDeath.containsKey( splitSecondPlayer[1].trim() ) )
						meansOfDeath.put( splitSecondPlayer[1].trim(), 1 );
					else
						// Incrementa mais um para a causa da morte
						meansOfDeath.put( splitSecondPlayer[1].trim(), meansOfDeath.get( splitSecondPlayer[1].trim() ) + 1 );

				} else if ( mEndGame.find() ) {
					/*
					 * Regex verificou que o game acabou
					 */
					game.setTotalKills( totalKills );
					game.setKillsForPlayers( killsForPlayers );
					game.setMeansOfDeath( meansOfDeath );
					games.add( game );
				}
			}

		} finally {
			br.close();
		}

		/*
		 * Se fosse utilizar JSON
		 * 
		 * Gson gson = new GsonBuilder().setPrettyPrinting().create(); String
		 * gamesString = gson.toJson( games ); System.out.println( gamesString
		 * );
		 */
		return games;
	}

	private static void createLogParser( List<Game> games ) throws IOException {
		BufferedWriter writer = null;
		try {
			// Cria arquivo de log do parse
			String path = new SimpleDateFormat( "yyyyMMdd_HHmmss" ).format( Calendar.getInstance().getTime() );
			File logParser = new File( "parse_" + path + ".txt" );
			writer = new BufferedWriter( new FileWriter( logParser ) );
			for ( Game game : games ) {
				StringBuilder builderPlayers = new StringBuilder();
				StringBuilder builderKills = new StringBuilder();
				// Monta o log no formato correto
				for ( Map.Entry<String, Integer> entry : game.getKillsForPlayers().entrySet() ) {
					builderPlayers.append( "\"" + entry.getKey() + "\", " );
					builderKills.append( "\t\t\"" + entry.getKey() + "\": " + entry.getValue() + ",\n" );
				}
				String players = builderPlayers.substring( 0, builderPlayers.length() - 2 );
				String kills = builderKills.substring( 0, builderKills.length() - 2 );

				// Escreve no log cada game no formato correto
				writer.write( game.getName() + ": {\n\t total_kills: " + game.getTotalKills() + "\n\t players: [" + players + "]" + "\n\t kills: {\n" + kills + "\n\t}" + "\n}" );
				writer.newLine();
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			writer.close();
		}
	}

	private static void report( List<Game> games ) throws IOException {
		BufferedWriter writer = null;
		try {
			// Cria arquivo de relatorio
			String path = new SimpleDateFormat( "yyyyMMdd_HHmmss" ).format( Calendar.getInstance().getTime() );
			File logParser = new File( "report_" + path + ".txt" );
			writer = new BufferedWriter( new FileWriter( logParser ) );
			Map<String, Integer> players = new HashMap<String, Integer>();
			writer.write( "Relatório dos Games: \n" );
			for ( Game game : games ) {
				writer.write( game.toString() );
				writer.newLine();

				for ( Map.Entry<String, Integer> entry : game.getKillsForPlayers().entrySet() ) {
					if ( !players.containsKey( entry.getKey() ) )
						players.put( entry.getKey(), entry.getValue() );
					else
						players.put( entry.getKey(), players.get( entry.getKey() ) + entry.getValue() );
				}
			}
			Map<String, Integer> sortedPlayers = sortByKills( players );
			writer.write( "\nRanking Geral:\n" + sortedPlayers.toString() );

		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			writer.close();
		}
	}

	private static Map<String, Integer> sortByKills( Map<String, Integer> unsortMap ) {

		// Converte Map para List
		List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>( unsortMap.entrySet() );

		// Classifica a List comparandos os values (kills)
		Collections.sort( list, new Comparator<Map.Entry<String, Integer>>() {
			@Override
			public int compare( Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2 ) {
				return ( o2.getValue() ).compareTo( o1.getValue() );
			}
		} );

		// Converte a List para Map
		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for ( Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext(); ) {
			Map.Entry<String, Integer> entry = it.next();
			sortedMap.put( entry.getKey(), entry.getValue() );
		}
		return sortedMap;
	}

	private static void meansOfDeath( List<Game> games ) throws IOException {
		BufferedWriter writer = null;
		try {
			// Cria arquivo de relatorio das mortes
			String path = new SimpleDateFormat( "yyyyMMdd_HHmmss" ).format( Calendar.getInstance().getTime() );
			File logParser = new File( "meansDeath_" + path + ".txt" );
			writer = new BufferedWriter( new FileWriter( logParser ) );
			writer.write( "Relatório de Mortes: \n" );
			for ( Game game : games ) {
				StringBuilder builderDeaths = new StringBuilder();
				// Monta o log no formato correto
				for ( Map.Entry<String, Integer> entry : game.getMeansOfDeath().entrySet() ) {
					builderDeaths.append( "\t\t\"" + entry.getKey() + "\": " + entry.getValue() + ",\n" );
				}
				String kills = "";
				if ( builderDeaths.length() >= 2 )
					kills = builderDeaths.substring( 0, builderDeaths.length() - 2 );

				// Escreve no log cada game com as causas da morte no formato
				// correto
				writer.write( game.getName() + ": {\n\t kills_by_means: " + "{\n" + kills + "\n\t}" + "\n}" );
				writer.newLine();
			}

		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			writer.close();
		}
	}
}
