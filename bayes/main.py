# NBC w wersji dyskretnej
# wczytac dane
# macierz 178x13 i 178x1
# podzial na czesc uczaca i testowa
# funkcja dyskretyzacji (liczba koszykow i minima i maksima referencji) equal width binning
# klasa reprezentujaca naiwny klasyfikator Bayesa ze zmiennymi dyskretnymi
# - dziedziczenie po baseestimator i classifiermixin
# - metoda fit (uczenia)
# - metoda predict (klasyfikowanie)
# - pomocniczo predict_proba
# - rozklad a priori klas (struktura jednowymiarowa) tablica - liczba klas
# - rozklady warunkowe dla poszczegolnych cech i klas (struktura trojwymiarowa) tablca - liczba klas, liczba cech, liczba koszykow  
# wyznaczyc dokladnosc otrzymanego klasyfikatora na zbiorach uczącym i testowym
# poprawka laplace'a np w konstruktorze fladzka

import numpy as np
from sklearn.base import BaseEstimator, ClassifierMixin
from sklearn.model_selection import train_test_split

data = np.genfromtxt('wine.data', delimiter=',')


def dyskretyzacja(X, n_koszykow, min_ref, max_ref):
    """ zmienia dane na dyskretne
    n_koszykow - przedzialy do dyskretyzacji, liczba kolumn w macierzy X
    min_ref - wartości minimalne dla każdej kolumny
    max_ref - wartości maksymalne dla każdej kolumny
    """
    X_dyskretne = np.zeros_like(X, dtype=int)
    for i in range(X.shape[1]):
        bins = np.linspace(min_ref[i], max_ref[i], n_koszykow + 1)
        X_dyskretne[:, i] = np.clip(np.digitize(X[:, i], bins[:-1]) - 1, 0, n_koszykow - 1)
    return X_dyskretne


X = data[:, 1:]
y = data[:, 0]
min_ref_ = np.min(X, axis=0)
max_ref_ = np.max(X, axis=0)  # wzdluz kolumn
X_dyskretne = dyskretyzacja(X, 8, min_ref_, max_ref_)

X_train, X_test, y_train, y_test = train_test_split(X_dyskretne, y, test_size=0.3)


class Bayes(BaseEstimator, ClassifierMixin):
    def __init__(self, n_koszykow=5, poprawka_laplacea=False):
        self.n_koszykow = n_koszykow
        self.poprawka_laplacea = poprawka_laplacea  # flaga czy uzyc poprawki laplaca

    def fit(self, X, y):
        '''uczy na danych treningowych'''

        self.min_ref_ = np.min(X, axis=0)
        self.max_ref_ = np.max(X, axis=0)  # wzdluz kolumn
        # X_dyskretne = dyskretyzacja(X, self.n_koszykow, self.min_ref_, self.max_ref_)

        self.klasy_ = np.unique(y)

        # prawdopodobienstwo a priori
        # P(Y=y)
        self.prior_ = np.zeros(len(self.klasy_))
        for i, klasa in enumerate(self.klasy_):
            self.prior_[i] = np.mean(y == klasa)

        # rozklad warunkowy
        # P(X=x|Y=y)
        self.likelihood_ = np.zeros((len(self.klasy_), X.shape[1], self.n_koszykow))  # czemu selflikehood?

        for i, klasa in enumerate(self.klasy_):
            czy_klasa = (y == klasa)
            X_klasy = X[czy_klasa]

            for j in range(X.shape[1]):
                for k in range(self.n_koszykow):
                    if self.poprawka_laplacea:
                        licznik = np.sum(X_klasy[:, j] == k) + 1
                        mianownik = len(X_klasy) + self.n_koszykow
                    else:
                        licznik = np.sum(X_klasy[:, j] == k)
                        mianownik = len(X_klasy)

                    self.likelihood_[i, j, k] = licznik / mianownik

        return self

    def predict_proba(self, X):
        '''klasyfikowanie oblicza prawdopodobienstwa przynależności do klas'''
        X_dyskretne = dyskretyzacja(X, self.n_koszykow, self.min_ref_, self.max_ref_)

        prawdopodobienstwa = np.zeros((X.shape[0], len(self.klasy_)))

        for i in range(X.shape[0]):  # wiersze
            for j, klasa in enumerate(self.klasy_):
                p = self.prior_[j]
                for k in range(X.shape[1]):  # kolumny
                    wartosc = X_dyskretne[i, k]
                    p *= self.likelihood_[j, k, wartosc]
                prawdopodobienstwa[i, j] = p

        suma = np.sum(prawdopodobienstwa, axis=1)
        niezero = suma != 0

        prawdopodobienstwa[niezero] /= suma[niezero, np.newaxis]
        prawdopodobienstwa[~niezero] = 1.0 / len(self.klasy_)

        return prawdopodobienstwa

    def predict(self, X):
        '''wybiera klase na podstawie najwiekszego prawdopodobienstwa'''
        # p=argmax(P(X=x|Y=y))
        return self.klasy_[np.argmax(self.predict_proba(X), axis=1)]


# bez
nbc = Bayes(n_koszykow=8, poprawka_laplacea=False)
nbc.fit(X_train, y_train)

dokladnosc_uczacy = np.mean(nbc.predict(X_train) == y_train)
dokladnosc_testowy = np.mean(nbc.predict(X_test) == y_test)

print("\nbez poprawki:")
print(f"zbior uczacy: {dokladnosc_uczacy:.4f}")
print(f"zbior testowy: {dokladnosc_testowy:.4f}")

# z poprawka
nbc_laplace = Bayes(n_koszykow=8, poprawka_laplacea=True)
nbc_laplace.fit(X_train, y_train)

dokladnosc_uczacy_laplace = np.mean(nbc_laplace.predict(X_train) == y_train)
dokladnosc_testowy_laplace = np.mean(nbc_laplace.predict(X_test) == y_test)

# uczaca w dol
# testowa w gore
print("\nz poprawka:")
print(f"zbior uczacy: {dokladnosc_uczacy_laplace:.4f}")
print(f"zbior testowy: {dokladnosc_testowy_laplace:.4f}")
