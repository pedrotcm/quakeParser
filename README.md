# quakeParser
Parser para arquivo de log do quake 3 arena.
https://github.com/pedrotcm/quakeParser

-SETUP
1- Importar o projeto como Maven Project.
2- Executar a Classe Principal "App.java".

-RESULTADOS
1-Após a execução da Classe Principal será criado 3 arquivos na pasta do projeto:
parse_.txt (Task 1)
report_.txt (Task 2)
meansDeath_.txt (Plus)

-SOBRE
A classe App possui o método "parse" que ler o arquivo de log "games.log" e coleta as informações de cada jogo retornando uma lista de Games.
Os métodos createLogParser, report e meansOfDeath recebem a lista de games e são responsavéis por criarem os arquivos correspondentes aos resultados.
