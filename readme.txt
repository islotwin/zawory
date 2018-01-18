
Opis problemu
Aby znaleźć najmniejszy podzbiór zaworów, które odetną grupę wybranych wierzchołków od dopływu do wody, sprowadzam problem do postaci sieci przepływowej i szukania minimalnego przecięcia w grafie.

Sieć przepływowa
1. Źródła tworzą 1 wierzchołek S, wielokrotne krawędzie są usuwane
2. Wierzchołki do usunięcia tworzą 1 wierzchołek T, wielokrotne krawędzie są usuwane
3. Wierzchołki i krawędzie (o nieskończenie dużej przepustowości) są dodawane do sieci, idąc rekurencyjnie przez krawędzie wychodzące ze źródła
4. Wierzchołki bez krawędzi wychodzących są usuwane

Maksymalny przepływ
1. Wierzchołki będące zaworami dzielone są na 2 wierzchołki połączone 1 krawędzią o przepustowości 1
2.  Algorytm Dinica - maksymalny przepływ mówi o minimalnej liczbie zaworów potrzebnych do odcięcia spływu
3. Zawory, do których da się dotrzeć po ostatniej iteracji, po usunięciu zaworów nadmiarowych, są rozwiązaniem

Brute force
1. Binarny ciąg reprezentujący zawory (ograniczenie górne do 63 zaworów w grafie)
2. Sprawdzanie czy dany zbiór zaworów rozcina graf (zaczynając od podzbioru n-1 elementowego, gdzie n jest liczbą zaworów), jeśli tak, próba znalezienia mniejszego podzbioru o takiej własności
3. Najmniejszy znaleziony zbiór jest rozwiązaniem

Tryby wykonania
-m1 < filename.txt (czytanie grafu z pliku)
-m2 n m (generowanie grafu: n - liczba zaworów w grafie, m - współczynnik gęstości grafu)
-m3 m n o p r(generowanie wielu przypadków grafów: n - liczba skoków, m - początkowa liczba zaworów w grafie, o - wielkość skoku liczby zaworów, p - liczba powtórzeń dla każdej wielkości n, r - gęstość grafu)

Wejście
Wejściem jest graf w postaci:

(Nr wierzchołka) (typ)
-
(wierzchołek początkowy krawędzi) (wierzchołek końcowy krawędzi)
-
(Nr wierzchołka do odcięcia od źródła) (kolejne wierzchołki)

Typ wierzchołka:
	1. Źródło - s
	2. Odpływ - l
	3. Zawór - v

Wyjście
W zależności od trybu programu wyjściem jest zbiór zaworów, które trzeba zakręcić aby odciąć od źródła wybrany podzbiór wierzchołków i czas wykonania algorytmu lub tabela porównująca złożoność i czasy wykonania dla różnych wielkości problemu.
