# OOPProject

TYTUŁ: SYMULACJA ŚWIATA SAMOLOTÓW, STATKÓŒ I PASAŻERÓW.
WYKONAŁ: KAMIL PIOTROWSKI

Aplikacja powstała na zaliczenie przedmiotu Programowanie Obiektowe.

Krótka instrukcja obsługi:

	GUI (Swing):
	
		1. 	Po najechaniu na lewą krawędź okna wyświetla się lista pasażerów.
			Kliknięcie na daną osobę wyświetla jej dane w prawym boxie informacyjnym.
			
		2.	Mapa świata - Na mapie są rysowane obiekty w które można kliknąć.
			Kliknięcie powoduje wyświtlenie informacji o obiekcie.
			
		3.	Panel akcji - 4 przyciski które umożliwiają dodanie pojazdów.
		
		4.	Akcje pojazdu. Po kliknięciu na pojazd na mapie wyświtlaja sie o nim informacje
			wraz z możliwymi przyciskami akcji. Działanie przycisków zostanie omówione w dalszej cześci.
			
	PASAŻEROWIE: 
	
		1.	Pasażerowie są tworzeni automatycznie wraz ze stworzeniem pojazdu pasażerskiego.
			Wszelkie wartości są losowane, użytkownik nie ma wpływu na zmianę danych pasażera.
			
	DROGI/MIASTA:
	
		1.	Drogi i miasta są z góry zdefiniowane, użytkownik nie ma na nie wpływu!
			
	POJAZDY:
	
		1. 	Pojazd pojawia się po kliknięciu w odpowieni przycisk akcji.
		
		2.	Wszystkie pojazdy rysowane są na mapie.

		3. 	Pojazdy pasażerskie dodatkowo tworzą losową liczbę pasażerów.
		
		4. 	Usunięcie pojazdu następuje po kliknięciu w odpowiedni przycisk akcji.
			Jeżeli usuwany zostaje pojazd pasażerski zabijani są rownież pasażerowie!!!
			
		5.	Dla każedego pojazdu można zmienić trasę. 
			Zmiana ta następuje losowo. Nie jest możliwa zmiana trasy dla pojazdu, 
			który znajduje się już na ostatnim odcinku drogi do miasta docelowego.
			
	AWARYJNE LĄDOWANIE:
	
		1.	Ten przycisk akcji występuje tylko dla samolotów.
		
		2.	Powoduje, że samolot awaryjnie ląduje na najbliższym lotniku, a następnie wraca na trasę docelową.
		
		3. 	!!!UWAGA:
		
			- 	Wywołanie awaryjnego lądowania na skrzyżowaniu spowoduje, że zostanie ono wykonane po 
				opuszczeniu skrzyżowania.
				
			-	Samolot może zdecydować, że powinien zawrócić.
				Pojazd będzie czekał dopóki będzie mógł zawrócić (w odpowiednim miejscu na trasie przeciwległej nie będzie samolotu)
				W krytycznej sytuacji może do doprowadzić do zakleszczenia!
				Z TEGO WZGLĘDU AWARYJNE LĄDOWANIE POWINNO BYĆ UŻYWANE Z ROZSĄDKIEM!
				
	ZAKLESZCZENIA:
		
		1.	Wszelkie zakleszczenia można naprawić poprzez usunięcie pojazdu który powoduje problem :)
		
	SERIALIZACJA:
	
		1.	Aby dokonać zapisu symulacji należy kliknąć w czerwony napis "zapisz" w lewym górnym rogu programu.
		
		2.	Wczytanie symulacji następuje po kliknięciu na przycisk "wczytaj"
		
		3.	Użytkownik nie ma mozliwości wyboru pliku, serializacja zawsze wczytywana jest z ostatniego zapisu!
		
	
				
				 	
		
