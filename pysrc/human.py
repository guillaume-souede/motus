# coding: utf-8
"""
Bibliotheque 'human' qui contient l'interface pour les
méthodes du joueur en mode 'Human vs IA'.
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

from gameboard import GameBoard
from gui_tools import My_MessageBox
from configs import *

class Human_Player():
    
    def __init__(self, master:tk.Tk, gameboard:GameBoard):
        
        self.__master = master
        self.__dico_Letters = master.dico_Letters
        self.__gameboard:GameBoard = gameboard
        self.__nb_letters = self.__gameboard.nb_Letters
        self.__nb_tries = self.__gameboard.nb_Tries
        self.__human_status:PlayerStatus = "idle"
        self.OK,self.IS,self.NO = 0,0,0             # - variables définissants le nombre et le type de lettres trouvées
        
    def valide_Mot(self, word:str) -> PlayerStatus:
        # - Recherche et écriture du mot dans les cases du premier mot libre --
        word_nbr, buttons = self.__find_free_word(word)
        # ---------------------------------------------------------------------
        buttons = self.__draw_OK_letters(word=word, buttons=buttons)
        buttons = self.__draw_IS_letters(word=word, buttons=buttons)
        buttons = self.__draw_NO_letters(word=word, buttons=buttons)
        if (word_nbr == self.__gameboard.nb_Tries -1) or (self.OK == self.__nb_letters):
            self.__human_status = self.__win_loose_game()
        else:
            self.__human_status = "idle"
        return self.__human_status
    
    def __win_loose_game(self) -> PlayerStatus:
        """ Methode qui renvoi le status du joueur : 'winner' ou 'looser' """
        return "loser"if self.NO > 0 or self.IS > 0 else "winner"
    
    def __draw_NO_letters(self, word:str, buttons:list) -> PlayerStatus:
        """ Changement de la couleur de fond, le relief des lettres
            qui ne sont pas dans le mot MOTUS.
        """
        for idx,button in buttons:
            button.configure(bg=COLOR_NO,relief='flat',activebackground=COLOR_NO)
            button.flash()
            self.NO = len(buttons)
        return buttons
        
    def __draw_IS_letters(self, word:str, buttons:list):
        """ Recherche de la/des lettres/position qui sont dans le mot 
            et change la couleur de fond, le relief de ces lettres.
        """
        found:list = ([])
        for idx,button in buttons:
            if button.cget('text').lower() in self.__master.MOTUS_word:
                button.configure(bg=COLOR_IS,relief='flat',activebackground=COLOR_IS)
                found.append((idx,button))
                button.flash()
        self.IS = len(found)
        [buttons.remove(b) for b in found[::-1]]
        return buttons
    
    def __draw_OK_letters(self, word:str, buttons:list):
        """ Recherche de la/des lettres/position dans le mot qui est/sont bien placée(s)
            et change la couleur de fond, le relief de ces lettres.
        """
        found:list = ([])
        for idx,button in buttons:
            if button.cget('text').lower() == self.__master.MOTUS_word[idx[0] % self.__nb_letters]:
                button.configure(bg=COLOR_OK,relief='flat',activebackground=COLOR_OK)
                found.append((idx,button))
                button.flash()
        self.OK = len(found)
        [buttons.remove(b) for b in found[::-1]]        
        return buttons
        
    def __find_free_word(self, proposition:str) -> list:
        """ Recherche de la position du premier mot libre de 0 à 6...9 """
        dummy:list = ([])
        word_nbr:int = -1
        records = list(filter(lambda record:record[1]==False, self.__dico_Letters.values()))
        if records:
            for l in range(self.__nb_letters):
                btn_ID, tries, letter = records[l][0]
                self.__dico_Letters[tries,l] = ((btn_ID,tries,proposition[l]), True, records[l][2])
                self.__dico_Letters[tries,l][2].configure(text=proposition[l].upper())
                dummy.append((records[l][0],records[l][2]))
            word_nbr = records[0][0][1]
            self.__master.vrequest.set("")
        return word_nbr, dummy
        
        
if __name__ == "__main__":
    
    root = tk.Tk()
    root.dico_Letters = {}
    root.dico_MOTUS = {}
    root.MOTUS_word = "Motus!"
    
    gameboard = GameBoard(root, None)
    human = Human_Player(root, gameboard)
