# Lab02 - ZADANIE OPTYMALIZACYJNE

Polecenie:
Załóżmy, że na jakiejś plaży zorganizowano konkurs budowy wież z piasku. Każdy z uczestników konkursu otrzymuje [n] wiaderek. W każdym wiaderku znajduje się piasek opisany dwoma atrybutami: kąt zsypu [a], objętość [v].

Każdy z uczestników konkursu może usypywać wieże z piasku w wyznaczonych [m] miejscach. Każde miejsce ma kształt koła o ograniczonym od góry rozmiarze. Ograniczenie to wyrażone jest maksymalną wartością promienia danego koła [r].

Usypywanie wież odbywać się ma warstwami. Warstwy powinny być wyrównywane od góry i nie zachodzić na siebie. Stąd każda warstwa w przekroju (pionowym, przechodzącym przez średnicę koła stanowiącego podstawę wieży) powinna mieć postać trapezu równoramiennego. W efekcie przekrój danej wieży powinien wyglądać jak stos poukładanych na sobie trapezów. W szczególności przekrój najwyższej warstwy może być trójkątem równoramiennym.

Zadaniem uczestników konkursu jest spełnienie następujących wymagań:
usypać wieże, których uśredniona wysokość będzie jak największa,
usypać wieże, by sumarycznie w wiaderkach pozostało jak najwięcej piasku.
Proszę zauważyć, że mamy tu do czynienia z problemem optymalizacyjnym, który może mieć wiele rozwiązań zależnych od danych wejściowych. Punkty 1 i 2 można potraktować jako kryteria (k1 = średnia wysokość usypanych wież, k2 = sumaryczna objętość piasku pozostałego w wiaderkach), którym można przypisać wagi (odpowiednio w1 i w2). Zadanie optymalizacji polega tu na znalezieniu MAX (w1*k1+w2*k2). Konkurs wygrywa ten, komu uda się osiągnąć najlepszy wynik.

Podczas rozwiązywania zadania proszę przyjąć założenie, że piasek usypywany jest porcjami (np. 0,5 jednostki objętości). Taka dyskretyzacja pozwolić ma na zaimplementowanie algorytmu przeszukującego przestrzeń rozwiązań o ograniczonym rozmiarze.

Dane opisująca problem do rozwiązania przez danego uczestnika konkursu powinny być zapisane w dwóch plikach:
wiaderka.txt o zawartości:
# nr wiaderka, kąt zsypu, objętość
1, 15, 10
2, 30, 40
...
miejsca.txt o zawartości:
# nr miejsca, promień
1, 13
2, 10
...
Wagi w1 i w2 występujące przy kryteriach można wczytać z linii komend podczas uruchamiania programu, wczytać ze standardowego wejścia podczas działania programu, wczytać z dodatkowego pliku.

[Moje rozwiązanie](src)