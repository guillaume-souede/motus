from typing import Literal

WordLength = Literal["6", "7", "8", "9"]
wordlengthlist:list=[c for c in range(6,10)]

PlayerMode = Literal["human","computer"]
PlayerStatus = Literal["winner","loser","idle"]
PlayerGenre = Literal["homme","femme"]

dico_path:str = "data"
images_path:str  = "images"

default_help_filename = "rules.txt"
default_dico_filename = "motsMotus.txt"
default_MOTUS_background = "defaut.png"

COLOR_OK = "red"
COLOR_IS = "lightblue"
COLOR_NO = "yellow"

if __name__ == "__main__":
    
    print(f"WordLength: {WordLength}")
    print(f"wordlengthlist: {wordlengthlist}")
    print(f"player_status: {PlayerStatus}")
    print(f"player_mode: {PlayerMode}")
    print(f"dico_path: {dico_path}")
    print(f"images_path: {images_path}")
    print(f"default_help_filename: {default_help_filename}")
    print(f"default_dico_filename: {default_dico_filename}")
    print(f"default_MOTUS_background: {default_MOTUS_background}")