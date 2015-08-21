# quakeParser
Parser para arquivo de log do quake 3 arena.
https://github.com/pedrotcm/quakeParser

<h3>SETUP</h3>
1- Importar o projeto como Maven Project.</br>
2- Executar a Classe Principal "App.java".

<h3>RESULTADOS</h3>
  Após a execução da Classe Principal será criado 3 arquivos na pasta do projeto:</br>
parse_.txt (Task 1)</br>
report_.txt (Task 2)</br>
meansDeath_.txt (Plus)</br>

<h3>SOBRE</h3>
  A classe App possui o método "parse" que lê o arquivo de log "games.log" e coleta as informações de cada jogo retornando uma lista de Games.</br>
  Os métodos createLogParser, report e meansOfDeath recebem a lista de games e são responsavéis por criarem os arquivos correspondentes aos resultados.
