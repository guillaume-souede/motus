#!/usr/bin/python3
# -- encode utf-8 --
"""
Bibliotheque 'GUI_Tools'. Quelques surcharges de classes tKinter 
pour le jeu MOTUS inspiré du jeu télévisé diffusé sur France2
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

import re
import os.path as op
import tkinter as tk

from os import getcwd
from configs import *

class My_LabelFrame(tk.LabelFrame):

    def __init__(self,master,col=0,row=0,cspan=1,rspan=1,pad=(0,0,0,0),sticky='nsew', *args, **kwargs):

        tab_options:dict = {'bg':'ivory', 'bd':3, 'relief':'groove', 'labelanchor':'n'}
        for key in list(tab_options.keys()):
            if kwargs.get(key, None) == None: kwargs[key] = tab_options.get(key, None)

        tk.LabelFrame.__init__(self,master,*args,**kwargs)        

        self.grid(column=col, row=row, columnspan=cspan, rowspan=rspan,
                                  padx=pad[0], pady=pad[1], ipadx=pad[2], ipady=pad[3], sticky=sticky)
        [self.grid_rowconfigure(r, weight=1) for r in range(rspan)]

    def name(self):
        return f"{self.master}."+self._name
        
    
class Window_StateBar(tk.Frame):
    
    def __init__(self, master, message, waitime, col=0, row=0, cspan=1, sticky="nsew",
                        defMessage:str=' Info : ', defTime:int=10,*args,**kwargs):
        
        self.__master = master
        self.__message = message
        self.__waitnbr:int = None
        self.__defaultTime = defTime
        self.__defaultMsg = defMessage
        self.__vl_texte = tk.StringVar()
        self.__wait = waitime

        tab_options:dict = {'bd':1, 'bg':'tan2', 'relief':'groove'}        
        for key in list(tab_options.keys()):
            if kwargs.get(key, None) == None: kwargs[key] = tab_options.get(key, None)
        super().__init__(master, *args, **kwargs)
        
        self.grid(column=col,row=row,columnspan=cspan,padx=2,pady=2,sticky=sticky)
        tk.Label(self,bd=0,bg=self.cget('bg'),anchor="sw",height=1,textvariable=self.__vl_texte).grid()
        self.update_vltexte(defMessage if not message else message, waitime)

    @property
    def get_message(self) -> str:
        return self.__vl_texte.get()
    @get_message.setter
    def get_message(self, default_msg:str):
        self.__defaultMsg = default_msg
        
    def __raz_vltexte__(self):
        self.__vl_texte.set(self.__defaultMsg)
        self.__waitnbr = None
    
    def update_vltexte(self, msg:str, wait=10):
        if self.__waitnbr != None:
            self.after_cancel(self.__waitnbr)
        if wait == 0:
            return
        elif wait > 1:
            self.__waitnbr = self.after(self.__defaultTime if wait==None else (wait*1000) , self.__raz_vltexte__)
        self.__vl_texte.set(msg)


class Win_MessageBox(tk.Toplevel):
    
    def __init__(self, master:tk.Tk, message:str=None, *args, **kwargs):
        
        self.__master = master
        self.__name__ = '!win_messagebox'
        self.__vmessage = tk.StringVar(value=message)
        
        tab_options:dict = {'bd':3, 'bg':'wheat', 'relief':'ridge', 'pady':5}        
        for key in list(tab_options.keys()):
            if kwargs.get(key, None) == None: kwargs[key] = tab_options.get(key, None)
        super().__init__(master, *args, **kwargs)
        
        self.protocol("WM_DELETE_WINDOW", self.Quit)
        self.resizable(False, False)
        
        tk.Message(self,bg='wheat',width=400,aspect=100,justify=tk.CENTER,
                                           textvariable=self.__vmessage).grid(padx=10,pady=10)
        tk.Button(self,width = 8,bg='tan',text='Ok',command=self.Quit).grid()
        self.bind('<Return>', self.Quit)
        self.withdraw()
    
    @property
    def message(self)->str:
        return self.__vmessage.get()
    @message.setter
    def message(self, message):
        self.__vmessage.set(message)
        if not self.winfo_ismapped():
            self.deiconify()
        self.update_idletasks()
        
    def boxtitle(self, title:str):
        self.title(title) 
    
    def Quit(self, event=None):
        self.boxtitle(" Message ")
        self.withdraw()    
   

class My_MessageBox(tk.Toplevel):
    
    def __init__(self, master:tk.Tk, title:str, message:str=None, *args, **kwargs):
        
        self.__master = master
        self.__vtitle = tk.StringVar(value=title)
        self.__vmessage = tk.StringVar(value=message)
        # ---------------------------------------------------------------------
        tab_options:dict = {'bd':3,'bg':'wheat','relief':'ridge','name':"!my_MessageBox"}        
        for key in list(tab_options.keys()):
            if kwargs.get(key, None) == None: kwargs[key] = tab_options.get(key, None)
        super().__init__(master, *args, **kwargs)
        # ---------------------------------------------------------------------        
        self.protocol("WM_DELETE_WINDOW", self.choose_cancel)
        msg_font = ('Courier\ New 18 bold italic')
        btn_font = ('Courier\ New 14 bold italic')
        self.bind_all("<Escape>", self.no_command)
        self.bind_all("<Return>", self.ok_command)
        self.title(self.__vtitle.get())
        self.resizable(False, False)
        # ---------------------------------------------------------------------
        tk.Message(self,bg='wheat',width=600,aspect=100,justify=tk.CENTER,font=msg_font,
                                               textvariable=self.__vmessage).grid(padx=10,pady=10,
                                                      column=0,row=0,columnspan=6,rowspan=4,sticky="nsew")
        self.playButton = tk.Button(self,text=" Rejouer ",width=12,font=btn_font,state="active",
                                            activebackground="lightgreen",command=self.ok_command,padx=10)
        self.playButton.grid(column=1,row=4,pady=10,sticky="nw")
        self.quitButton = tk.Button(self,text=" Quitter ",width=12,font=btn_font,
                                            activebackground="tan",command=self.no_command,padx=10)
        self.quitButton.grid(column=3,row=4,pady=10,sticky="ne")

    def valide_ok_command(self, event):
        print(f"event: {event}")

    def go(self):
        """ Methode qui permet de garder le focus sur la fenetre de choix
            de l'huile qui lors du choix renvoi le nom de l'huile choisie
            et ferme la fenetre Toplevel.
        """
        self.lift(self.__master)        # mise au premier plan de la Toplevel    
        self.how = None                 # Nom de la procédure exécutée en sortie
        self.mainloop()                 # Sortie de la Boucle principale par "self.quit(how)"
        self.destroy()                  # Fermeture de la fenetre Toplevel
        return self.how

    def choose_ok(self) -> str:
        return "yes"

    def choose_nok(self) -> str:
        return "no"
    
    def choose_cancel(self):
        self.Quit(None)
        
    def ok_command(self, event=None):
        self.Quit(self.choose_ok())
        
    def no_command(self, event=None):
        self.Quit(self.choose_nok())
        
    def Quit(self, how=None):
        """ Sortie de la boucle principale et non fermeture de la fenetre
            Exécution de la methode "how" qui permet de récupérer la
            donnée voulue en sotie en fin de méthode "go()"
        """
        self.how = how
        self.quit()                     # Exit mainloop()
        

class Game_Rules(tk.Toplevel):
        
    def __init__(self, master, filename:str=None, *args, **kwargs):
         
        self.__master = master
        self.__filename = filename if filename else op.join(getcwd(),dico_path,default_help_filename)
        self.__vtitle = tk.StringVar(value="Aide de MOTUS v1.0.10 (c)AMOUROUX Bernard 05/2025")
        self.__dico_lines:dict[int:str] = ({})
        # ---------------------------------------------------------------------
        tab_options:dict = {'bd':3,'bg':'wheat','relief':'ridge','name':"!my_gameRules"}        
        for key in list(tab_options.keys()):
            if kwargs.get(key, None) == None: kwargs[key] = tab_options.get(key, None)
        super().__init__(master, *args, **kwargs)
        # ---------------------------------------------------------------------        
        self.protocol("WM_DELETE_WINDOW", self.Quit)
        # ---------------------------------------------------------------------        
        self.txt_font = ('Courier\ New 18 bold italic')
        self.bind_all("<Escape>", self.Quit)
        self.minsize(master.app_size[0]//3, master.app_size[1]//3)
        self.columnconfigure(index=0, weight=1)
        self.rowconfigure(index=0, weight=1)
        self.title(self.__vtitle.get())
        # ---------------------------------------------------------------------             self.create_widgets()
        self.__load_helpfile()
    
    def show_helpfile(self):
        self.create_widgets()
        
    def __load_helpfile(self):
        if not op.isfile(self.__filename):
            raise FileNotFoundError(f"Bad file name: {self.__filename}")
        with open(file=self.__filename, mode='rt', encoding="utf-8") as helpfile:
            for idx,line in enumerate(helpfile.readlines()):
                self.__dico_lines[idx] = line.rstrip()
    
    def create_widgets(self):
        versb = tk.Scrollbar(self, orient=tk.VERTICAL)
        versb.grid(column=1,row=0,sticky='nse')
        self.__text_help = tk.Text(self, bg='ivory',font=self.txt_font,wrap="word",width=90)
        self.__text_help.tag_configure("ok", background="lightgreen",border=2,relief='raised')
        self.__text_help.tag_configure("is", background="orange",border=2,relief='raised')
        self.__text_help.tag_configure("no", background="red",border=2,relief='raised')
        self.__text_help.grid(column=0,row=0,sticky="nsew")
        self.__text_help['yscrollcommand'] = versb.set   
        versb['command'] = self.__text_help.yview
        # ---------------------------------------------------------------------        
        [self.__text_help.insert(tk.END, f"{line}\n") for line in self.__dico_lines.values()]
        # ---------------------------------------------------------------------
        tags_list = self.__look_for_tags(self.__text_help.get("1.0", tk.END))
        for span,tagname in tags_list:
            self.__text_help.tag_add(tagname,self.__text_help.index(f"1.0 + {span[0]-2}c"), self.__text_help.index(f"1.0 + {span[1]-1}c"))
        self.__text_help.configure(state="disabled")
    
    def __look_for_tags(self, help_text:str)->list:
        tags_list:list = ([])
        text_to_tag = [("Carré Vert  ","ok"),("Carré Orange","is"),("Carré Rouge ","no")]
        for color,tagname in text_to_tag:
            found = re.search(color, help_text, flags=0).span()
            tags_list.append((found,tagname))
        return tags_list
        
    def Quit(self, event=None):
        self.destroy()
    
# ----------------------------- Méthodes diverses -----------------------------    
def show_rules(master)->tk.Toplevel:
    print(" ---> show Rules ...")

def get_widget(parent, pathname:str)->object:
    """ Retourne l'objet dont le nom est 'pathname' et qui appartient à 'parent' """
    try:
        widget = parent.nametowidget(pathname)
        return widget
    except KeyError:
        return None



if __name__ == "__main__":
        
    root = tk.Tk()
    root.app_size = root.maxsize()
    msgbox = Win_MessageBox(root)
    msgbox.message = "Win Message Box"
    #print(f"msgbox name  : {msgbox}")
    #print("msgbox object:", get_widget(root,'.!win_messagebox').__repr__())
    msgbox.lift(root)
    message = f"\n{'Vous avez trouvé le mot MOTUS':100}\n{'Nouvelle partie ?':100}\n"
    #print(My_MessageBox(root,"Faites votre choix de partie",message).go())
    Game_Rules(root).show_helpfile()
    root.mainloop()
