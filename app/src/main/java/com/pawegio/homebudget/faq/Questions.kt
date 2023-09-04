package com.pawegio.homebudget.faq

data class Question(val question: String, val answer: String)

val questions = listOf(
    Question(
        "Po połączeniu z arkuszem pojawia się błąd, że nie udało się pobrać danych budżetowych. Jak rozwiązać ten problem?",
        """
            Aplikacja pobierając dane zakłada, że:
            • Nazwy arkuszy dla poszczególnych miesięcy nie zostały zmienione (czyli pozostają w j. polskim "Styczeń", "Luty" tak jak w oryginalnym formacie).
            • Każda kategoria ma maksymalnie 10 podkategorii zgodnie z oryginalnym formatem.
            • Kategorie i podkategorie występują w wierszach indeksowanych zgodnie z oryginalnym formatem (oczywiście można zmieniać nazwy kategorii).
            • Dni miesiąca występują w kolumnach indeksowanych zgodnie z oryginalnym formatem.
            Jeśli format danych w komórkach arkusza nie zgadza się z powyższymi założeniami to powoduje to błąd w aplikacji.
        """.trimIndent()
    ),
    Question(
        "Mam problem z połączeniem arkusza w formacie Excel. Jak go rozwiązać?",
        "Aplikacja działa tylko z arkuszami Google. Niestety wsparcie dla plików w formacie Excel jest niemożliwe ze względu na brak dostępnego interfejsu do komunikacji/wymiany danych."
    ),
    Question(
        "Udało mi się połączyć arkusz, ale nie moge dodawać nowych transakcji z poziomu aplikacji. Jak to rozwiązać?",
        "Spróbuj się wylogować i zalogować ponownie w aplikacji. Dodawanie wydatków wymaga nadania aplikacji uprawnienia edycji arkusza (uprawnienie nadawane jest przy pierwszym wejściu w ekran dodawania wydatków). Możliwe, że dane aplikacji po przyznaniu uprawnienia nie zostały odświeżone."
    )
)
