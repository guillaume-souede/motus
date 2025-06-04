# coding: utf-8
"""
Bibliotheque 'Handle_Dico'. Lecture et chargement du dictionnaire
des mots pour le jeu MOTUS inspiré du jeu télévisé diffusé sur France2.
Copyright (C) 2025  Bernard AMOUROUX

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

DNA_GBRecords_GUI v2 (C) 2025  Bernard AMOUROUX
This program comes with ABSOLUTELY NO WARRANTY; for details type `show w'.
This is free software, and you are welcome to redistribute it
under certain conditions; type `show c' for details.
"""

__author__ = "Bernard AMOUROUX"
__date__ = "$Date: 2025/05/18 07:00 $"
__copyright__ = "Copyright (c) 2025 Bernard AMOUROUX"
__license__ = "GPL 3"

import os.path as op
import tkinter as tk

from os import getcwd
from unicodedata import normalize,category
from random import shuffle,choice,seed
from configs import *
# ---- For test only ----
#seed(1)


class Handle_DicoMotus():
    
    @classmethod
    def valid_player_MOTUS(cls, proposition:str, dico:dict, word_length:WordLength) -> bool:
        return proposition in dico[word_length]
    
    def __init__(self, master:tk.Tk, filename:str=None):
        
        self.__master = master      # pour développement futur, sera déjà déclaré !
        self.__filename = filename if filename != None else default_dico_filename
        self.__dico_MOTUS:dict[str:[list]] = ({})
        self.__load_dicofile()
        # ---- for test only ----
        #seed(1)
    
    def __load_dicofile(self):
        """ Lecture du fichier des mots français avec normalisation au format "NFC"
            caractères accentués normalisée unicode UTF-8 ou format "NFD" par 
            décomposition des caractères accentués et filtrage de tous les 
            caractères de type "Mn" (marques non espacées = accents).
            Création du dictionnaire des mots de longueur 6 à 9 lettres pour MOTUS.
        """
        fname = op.join(getcwd(), dico_path, self.__filename)
        if op.isfile(fname):
            with open(fname, mode="rt", encoding='utf-8') as motusfile:
                for word in motusfile:
                    # ---------------------------------------------------------
                    # -- Lecture du mot puis normalisation avec/sans accents --
                    #word = ''.join(c for c in normalize('NFD', word) if category(c) != 'Mn')
                    word = normalize('NFC', word).strip()  # -- ici avec les accents --
                    # ---------------------------------------------------------
                    w = word; l = len(w)
                    if not l in range(6,10):
                        continue
                    else:
                        if not f"{l}" in self.__dico_MOTUS:
                            self.__dico_MOTUS[f"{l}"] = []
                        self.__dico_MOTUS[f"{l}"].append(w)
        else:
            raise FileNotFoundError(f" Fichier dictionnaire '{fname}' non trouvé !")
    
    @property
    def filename(self) -> str:
        return self.__filename
                    
    @property
    def dico_MOTUS(self)->dict:                                     # - return full dictionary
        """ Renvoi le dictionnaire complet des mots MOTUS """
        return self.__dico_MOTUS             
    
    def __dico_MOTUS_length(self, wordlength:WordLength)->list:     # - return value of dictionary[worldlength]
        """ Propriété qui renvoi la liste des mots du dictionnaire MOTUS 
            de longueur 'wordlength'.
        """
        return self.__dico_MOTUS.get(wordlength, [f"{wordlength}",])   
    
    def __shuffle_words(self, wordlength:WordLength)->list:
        """ Mélange la liste des mots MOTUS de longueur désirée 'wordlength'. 
            Si 'wordlength' est incorrect, renvoi 'str(wordlength)' comme mot.
        """
        shuffle(self.__dico_MOTUS_length(wordlength))
        return self.dico_MOTUS.get(wordlength, [f"{wordlength}",])
    
    def dico_MOTUS_one_word(self, wordlength:WordLength)->str:      # - return one word with desired length
        """ Renvoi un mot (aléatoire) du dictionnaire des mots MOTUS de longueur désirée 'wordlength'. """
        return choice(self.__shuffle_words(wordlength=wordlength))
    
    
if __name__ == "__main__":
    
    dico = Handle_DicoMotus(None, "motsMotus.txt")
    randomwords = [[print(dico.dico_MOTUS_one_word(f"{l}")) for l in range(6,10)] for _ in range(5)]
    [print(f"Nombre de mots de {l} lettres: {len(dico.dico_MOTUS[str(l)])}") for l in range(6,10)]
    print(f"'emirat' is on dico_MOTUS['6']: {Handle_DicoMotus.valid_player_MOTUS('emirat',dico.dico_MOTUS,'6')}")
    print(f"'élixir' is on dico_MOTUS['6']: {Handle_DicoMotus.valid_player_MOTUS('élixir',dico.dico_MOTUS,'6')}")
    