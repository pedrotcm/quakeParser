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
import java.util.HashMap;
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
						games.add( game );
						System.out.println( game );
					}
					game = new Game();
					totalKills = 0;
					killsForPlayers = new HashMap<String, Integer>();
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
				} else if ( mEndGame.find() ) {
					/*
					 * Regex verificou que o game acabou
					 */
					game.setTotalKills( totalKills );
					game.setKillsForPlayers( killsForPlayers );
					games.add( game );
					System.out.println( game );
				}
			}

		} finally {
			br.close();
		}
		return games;

		/*
		 * Se fosse utilizar JSON
		 * 
		 * Gson gson = new GsonBuilder().setPrettyPrinting().create(); String
		 * gamesString = gson.toJson( games ); System.out.println( gamesString
		 * );
		 */
	}

	private static void createLogParser( List<Game> games ) {
		BufferedWriter writer = null;
		try {
			// Cria arquivo de log do parse
			String path = new SimpleDateFormat( "yyyyMMdd_HHmmss" ).format( Calendar.getInstance().getTime() );
			File logParser = new File( path + ".log" );
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
			try {
				writer.close();
			} catch ( Exception e ) {
			}
		}
	}
}
