"""
Bibliotheque 'computer' qui contient l'interface pour les
méthodes de l'IA en mode 'IA vs Human'.
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

import tkinter as tk

from configs import *
from random import choice
from handledico import Handle_DicoMotus
from gameboard import GameBoard

class IA_Computer():
    
    def __init__(self, master:tk.Tk, gameboard:GameBoard):
        
        self.__master = master
        self.__name:str = "Jarvis"
        self.__gameboard = gameboard
        self.__dico_Motus:Handle_DicoMotus = master.dico_MOTUS  # - recupere le Handle_DicoMotus() du parent
        self.__dico_Buttons = self.__gameboard.dico_Buttons     # - dictionnaire des boutons du mot IA_word
        self.__nb_letters = gameboard.nb_Letters
        self.__nb_tries = gameboard.nb_Tries
        #self.__dico_IA_Words:dict = ({})                        # - dictionnaire des mots du joueur IA
        self.__MOTUS_word:str = ""                              # - Mot MOTUS que doit trouver l'IA
        # --------------------- 1er mot proposé par l'IA ----------------------
        self.__IA_word:str = self.__dico_Motus.dico_MOTUS_one_word(f"{self.__nb_letters}")
        #self.__dico_IA_Words.update({f"{self.__nb_letters}":master.dico_MOTUS.dico_MOTUS[f"{self.__nb_letters}"]})
        self.__list_IA_Words:list = master.dico_MOTUS.dico_MOTUS[f"{self.__nb_letters}"]
        
        # ---------------------------------------------------------------------
                
            
    def __valide_proposition(self, proposition:str)->bool:
        #return proposition in self.__dico_IA_Words[f"{self.__nb_letters}"]
        return proposition in self.__list_IA_Words
    
    def __win_loose_game(self) -> PlayerStatus:
        """ Methode qui renvoi le status du joueur : 'winner' ou 'looser' """
        return "winner" if self.OK == self.__nb_letters-1 else "loser"

    def __find_free_word(self, IA_word:str) -> list:
        """ Recherche de la position du premier mot libre de 0 à 6...9 """
        dummy:list = ([])
        records = list(filter(lambda record:record[1]==False, self.__dico_Buttons.values()))
        if records:
            for l in range(self.__nb_letters):
                btn_ID, tries, letter = records[l][0]   # -> 'records[l][0]:(18, 3, ' ')'
                self.__dico_Buttons[tries,l] = ((btn_ID,tries,IA_word[l]), True, records[l][2])
                self.__dico_Buttons[tries,l][2].configure(text=IA_word[l].upper())
                dummy.append(((btn_ID,tries,IA_word[l]),records[l][2]))
            self.TR = records[0][0][1]
        #print(f"dummy: {dummy} --> self.TR: {self.TR}")
        return self.TR, dummy
    
        
        
    def valide_Mot(self, human_word):
        resultat:PlayerStatus = "idle"
        if self.__valide_proposition(human_word):
            self.__MOTUS_word = human_word
            self.__master.validButton.configure(state='disabled')
            self.__master.entryRequest.configure(state='disabled')
            # -----------------------------------------------------------------
            while resultat == "idle":
                self.OK,self.IS,self.NO = 0, 0, 0
                print(f"self.__MOTUS_word: {self.__MOTUS_word} ---> self.__IA_word: {self.__IA_word}")
                word_nbr, buttons = self.__find_free_word(self.__IA_word)
                buttons = self.__draw_NO_letters(word=self.__IA_word, buttons=buttons)
                print(f"list_IA_Words: {self.__list_IA_Words}")
                buttons = self.__draw_IS_letters(word=self.__IA_word, buttons=buttons)
                buttons = self.__draw_OK_letters(word=self.__IA_word, buttons=buttons)
                
                if self.OK == self.__nb_letters:
                    resultat = "winner"
                    break
                elif word_nbr == self.__nb_tries-1 and self.OK < self.__nb_letters:
                    resultat = "loser"
                    break
                else:
                    # ---------- On choisit un autre mot de la liste ----------
                    self.__IA_word = choice(self.__list_IA_Words)
                    print(f"self.__IA_word: {self.__IA_word}")
            
            #print(f"self.__list_IA_Words: {self.__list_IA_Words}")        
            return resultat
        else:
            message = self.__master.barre_Etat.get_message
            self.__master.barre_Etat.update_vltexte(f" ---> le mot que vous venez de proposer '{human_word}' est invalide")
            self.__master.barre_Etat.get_message = message
        return resultat

    
    def __draw_NO_letters(self, word:str, buttons:list):
        bad_letters = set()
        for idx,button in buttons:
            if button.cget('text').lower() not in self.__MOTUS_word:
            #if idx[2] not in self.__MOTUS_word:
                button.configure(bg=COLOR_NO,relief='flat',activebackground=COLOR_NO)    
                bad_letters.add(button.cget('text').lower())
                button.flash()
                self.NO += 1
        self.__list_IA_Words = list(filter(lambda word:len([w for w in word if w in list(bad_letters)]) == 0 ,self.__list_IA_Words))        
        return buttons
        
    def __draw_IS_letters(self, word:str, buttons:list):
        for idx,button in buttons:
            if button.cget('text').lower() in self.__MOTUS_word:
                button.configure(bg=COLOR_IS,relief='flat',activebackground=COLOR_IS)
                button.flash()
                self.IS += 1
        return buttons
        
    
    def __draw_OK_letters(self, word:str, buttons:list):
        """ Recherche de la/des lettres/position dans le mot qui est/sont bien placée(s)
            et change la couleur de fond, le relief de ces lettres.
        """
        for idx,button in buttons:
            if button.cget('text').lower() == self.__MOTUS_word[idx[0] % self.__nb_letters]:
                button.configure(bg=COLOR_OK,relief='flat',activebackground=COLOR_OK)
                #self.__list_IA_Words = list(filter(lambda w:w[idx[0] % self.__nb_letters]!= \
                #                               button.cget('text').lower(), self.__list_IA_Words))
                button.flash()
                self.OK += 1
        return buttons
        
