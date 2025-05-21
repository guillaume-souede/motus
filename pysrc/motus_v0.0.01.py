#!/usr/bin/python3
# -- encode utf-8 --
"""
MOTUS - Une étude Python POO , adaptation du jeu télévisé "MOTUS"
sur France Télévision en mode graphique (TKinter) et Python 3.9.5
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

import sys,os
import os.path as op
import tkinter as tk
import tkinter.font as tkFont

from handledico import Handle_DicoMotus
from gameboard import GameBoard

from gui_tools import *
from configs import *


class Application(tk.Tk):
    
    def __init__(self, filename:str):
        
        tk.Tk.__init__(self)
        
        self.backImage = tk.PhotoImage(master=self,
                                file=op.join(os.getcwd(),images_path,default_MOTUS_background))
        
        self.labelFont = tkFont.Font(self, family='Courier New', size=10, weight='bold', slant='roman')
        self.menuFont = tkFont.Font(self, family='Serif', size=11, weight='normal', slant='italic')
        
        self.__dico_Letters:dict = ({})
        self.vnblettres = tk.IntVar(value=wordlengthlist[0])
        self.vnbessais = tk.IntVar(value=6)

        self.protocol('WM_DELETE_WINDOW',self.Quit)

        MAX_WIDTH, MAX_HEIGHT = self.maxsize()
        self.app_size = min(MAX_WIDTH-100,self.backImage.width()), min(MAX_HEIGHT-150,self.backImage.height())
        self.minsize(self.backImage.width()//2, self.backImage.height()//2)
        self.title("MOTUS v1.0 (c)AMOUROUX Bernard  Mai 2025")
        [self.columnconfigure(i, weight=1) for i in range(41)]
        [self.rowconfigure(i, weight=1) for i in range(41)]
        self.resizable(False, False)
        self.configure(bg='wheat')
        
        self.dico_MOTUS = Handle_DicoMotus(self, filename=filename)
        self.messageBox = Win_MessageBox(self)
        self.cree_widgets()
        
        
    def cree_widgets(self):
        # -----------------------------------------------------------------------------------------
        tk.Label(self, bd=0, bg='wheat',font=self.labelFont,
                              text=" Longueur du mot :").grid(column=0,row=0,columnspan=3,sticky="w")
        self.spboxletters = tk.Spinbox(self,bd=3,relief='sunken',textvariable=self.vnblettres, 
                                           wrap=True,from_=wordlengthlist[0],to=wordlengthlist[-1],
                                                width=3,state='readonly',font=('Arial 10 italic bold'))
        self.spboxletters.grid(column=3, row=0, sticky='w')    
        # -----------------------------------------------------------------------------------------
        tk.Label(self, bd=0, bg='wheat',font=self.labelFont,
                              text=" Nombre d'essais :").grid(column=4,row=0,columnspan=4,sticky="w")
        self.spboxtries = tk.Spinbox(self,bd=3,relief='sunken',textvariable=self.vnbessais,wrap=True,
                                     from_=6,to=10,width=3,state='readonly',font=('Arial 10 italic bold'))
        self.spboxtries.grid(column=8, row=0, sticky='w')
        # -----------------------------------------------------------------------------------------
        tk.Button(self,text='  Jouer  ',bg='wheat',activebackground='orange',state='active',
                    command=self.create_GameBoard).grid(column=12,row=0,padx=5,columnspan=5,sticky="nsew")
        tk.Button(self,text='Abandonner',bg='wheat',activebackground='red',state='disabled',
                    command=self.create_GameBoard).grid(column=34,row=0,padx=5,columnspan=5,sticky="nsew")
        # -----------------------------------------------------------------------------------------
    
        # --------------- Création du tk.Canvas() pour affichage de l'image de fond ---------------
        frame0 = My_LabelFrame(self,row=1,cspan=40,rspan=40,pad=(0,0,0,0),bd=2,relief='ridge')
        self.background = tk.Canvas(frame0, bd=3, relief='groove',name="!backImage",
                                                      width=self.app_size[0],height=self.app_size[1])
        self.background.grid(column=0, row=0, columnspan=40, rowspan=40, sticky='nsew')
        self.background.create_image(self.app_size[0]//2, self.app_size[1]//2, 
                                            image=self.backImage, anchor="center", tags='img_background')
        # -----------------------------------------------------------------------------------------
        self.gameBoard = GameBoard(frame0,self.__dico_Letters,self.vnblettres.get(),
                                      self.vnbessais.get(),col=9,row=30,cspan=20,rspan=10)
        self.gameBoard.create_GameBoard(self.gameBoard.bbox())
        self.__dico_Letters.update(self.gameBoard.dico_Buttons)
        # -----------------------------------------------------------------------------------------
        message = f" Info : Découvrir un MOTUS de {self.vnblettres.get()} lettres avec au maximum {self.vnbessais.get()} essais"
        self.barre_Etat = Window_StateBar(self,"",1,col=0,row=41,cspan=40,pady=5)
        self.barre_Etat.update_vltexte(message, 1)
        self.fenetre_a_propos(self.messageBox)    
    
    def create_GameBoard(self):
        dimensions = self.nametowidget(".!my_labelframe.gameBoard").bbox(); print(f"dimensions: {dimensions}")
            
    def fenetre_a_propos(self, msgbox:Win_MessageBox):
        """ Fenêtre-message à propos.
            Indique le nom de l'auteurs ainsi que la licence.
        """
        message = "MOTUS v0.0.10"+"\n\nCopyright (C) 2025\nBernard Amouroux\n" \
        "\nDonnées :\nDictionnaire des mots MOTUS\nhttps://www.motus.france2.fr\n\n" \
        "Sur une idée du projet 'MOTUS' de \nl'Université de Toulouse - Master BBS\nBio-informatique et Biologie des Systèmes\n-\n"\
        "https://www.univ-tlse3.fr/decouvrir-nos-diplomes/master-mention-bio-informatique\n" \
        "\nLicense : GPL Version 3, 29 June 2007"
        msgbox.boxtitle('À propos')
        msgbox.message = message
        msgbox.lift(self)
        
        
    
    def Quit(self):
        self.destroy()
    

if __name__ == "__main__":

    if len(sys.argv) > 1:
        filename = sys.argv[1] if op.isfile(sys.argv[1]) else None
    else: filename = None
        
    app = Application(filename)
    app.mainloop()


