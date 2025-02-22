# Lab07 - RMI

Podczas laboratorium należy zbudować aplikację działającą w środowisku rozproszonym, wykorzystującą do komunikacji RMI. Dokładniej - należy zaimplementować mały systemu, w którego skład wejdą podsystemy uruchamiane równolegle (na jednym lub na kilku różnych komputerach).
Zakładamy, że system będzie pełnić rolę symulatora interakcji zachodzących podczas wywozu nieczystości między wyróżnionymi podsystemami. Zasady jego działania mają być niemal takie same, jak opisano w treści zadania na laboratorium 6. Różnica polega na innym sposobie komunikacji. Tym razem zamiast bezpośrednio korzystać z gniazd należy wywoływać metody zadeklarowane w zdalnych interfejsach na obiektach namiastek. W architekturze pojawił się dodatkowy moduł - Krawiec (Tailor), który dostarcza rejestr rmi.

W implementacji systemu należy wykorzystać interfejsy dostarczone w bibliotece sewagelib-1.0-SNAPSHOT.jar. Proszę ten jar dołączyć do własnych projektów jako zależność. Proszę nie dołączać do projektu zawartych w tym jarze źródeł kodu. Źródła te wstawiono do jara, by było wiadomo, jakie interfejsy należy implementować. Nie można zmieniać tych interfejsów.

[Moje rozwiązanie](Lab07)