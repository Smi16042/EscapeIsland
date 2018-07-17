package view;

import control.InventoryControl;
import escapeIsland.EscapeIsland;
import exceptions.InventoryControlException;
import java.util.Scanner;
import model.*;
import view.BattleScene.*;
import java.util.Random;
import java.awt.Point;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Austin
 */
public class InteractWithEnviromentView extends View {

    public boolean doAction(String[] inputs) {

        Location currentLocation
                = EscapeIsland.getCurrentGame().getMap().getLocations()[Actor.Hero.getActorcoordinates().x][Actor.Hero.getActorcoordinates().y];

        char interactionsMenu = inputs[0].trim().toUpperCase().charAt(0);

        switch (interactionsMenu) {
            case 'C':
                if (currentLocation.getActor() != null) {
                    if (combatControls()) {
                        return false;
                    }
                } else {
                    this.console.println("Invalid Option");
                }
                break;
            case 'I':
                if (currentLocation.getItemRequired() != null) {
                    itemRequiredScene();
                } else {
                    this.console.println("Invalid Option");
                }
                break;
            case 'L':
                if (currentLocation.getObtainItem() != null) {
                    getLoot();
                } else {
                    this.console.println("Invalid Option");
                }
                break;
            case 'T':
                if (currentLocation.getTalkToNPC() != null) {
                    talkToNPC();
                } else {
                    this.console.println("Invalid Option");
                }
                break;
            case 'R':
                if (currentLocation.getRiddle() != null) {
                    riddle(EscapeIsland.getCurrentGame().getMap().getLocations()[(int) Actor.Hero.getActorcoordinates().getY()][(int) Actor.Hero.getActorcoordinates().getX()]
                            .getRiddle());
                } else {
                    this.console.println("Invalid Option");
                }
                break;
            case 'M':
                return true;
            default:
                this.console.println("Invalid Option");
        }

        return false;
    }

    public String[] getInputs() {
        String[] inputs = new String[1];

        Location currentLocation
                = EscapeIsland.getCurrentGame().getMap().getLocations()[Actor.Hero.getActorcoordinates().x][Actor.Hero.getActorcoordinates().y];

        this.console.println("***********************************************************"
                + "\n***********************************************************"
                + "\n*                                                         *"
                + "\n* Interactions                                            *"
                + "\n*                                                         *");

        if (currentLocation.getActor() != null) {
            this.console.println("* C - Combat ");
        };
        if (currentLocation.getItemRequired() != null) {
            this.console.println("* I - Item Required");
        }
        if (currentLocation.getObtainItem() != null) {
            this.console.println("* L - Loot");
        }
        if (currentLocation.getTalkToNPC() != null) {
            this.console.println("* T - Talk");
        }
        if (currentLocation.getRiddle() != null) {
            this.console.println("* R - Riddle");
        }

        this.console.println("\n* M - Return to map                                        *"
                + "\n*                                                         *"
                + "\n***********************************************************"
                + "\n***********************************************************");

        String[] menuItem = new String[1];
        try {
            menuItem[0] = this.keyboard.readLine();
        } catch (IOException ex) {
            Logger.getLogger(MainMenuView.class.getName()).log(Level.SEVERE, null, ex);
        }

        return menuItem;
    }

    private boolean combatControls() {

        Location location = EscapeIsland.getCurrentGame().getMap().getLocations()[Actor.Hero.getActorcoordinates().x][Actor.Hero.getActorcoordinates().y];

        Actor monster = location.getActor();
        while (deathScene(Actor.Hero, monster)) {

            Actor hero = EscapeIsland.getCurrentPlayer().getActor();

            this.console.println("***********************************************************"
                    + "\n***********************************************************"
                    + "\n*                                                         *"
                    + "\n*      Combat Scene                                       *"
                    + "\n*                                                         *");

            // get Hero Current HP, attack and defense
            this.console.println(EscapeIsland.getCurrentPlayer().getPlayersName() + "                                      " + monster.getActorName());
            this.console.println("Hit Points: " + Actor.Hero.getActorHitPoints() + "                            " + "Hit Points: " + monster.getActorHitPoints());
            this.console.println("Attack: " + BattleScene.totalAttack(Actor.Hero) + "                                 " + "Attack: " + BattleScene.totalAttack(monster));
            this.console.println("Defense: " + BattleScene.totalDefense(Actor.Hero) + "                                " + "Defense: " + BattleScene.totalDefense(monster));

            this.console.println("\n                                                          "
                    + "\n* A - Attack                                              *"
                    + "\n* D - Defend                                              *"
                    + "\n* I - Item                                                *"
                    + "\n* F - Flee                                                *"
                    + "\n*                                                         *"
                    + "\n***********************************************************"
                    + "\n***********************************************************");

            String talkToNPC = null;
            try {
                talkToNPC = this.keyboard.readLine();
            } catch (IOException ex) {
                Logger.getLogger(InteractWithEnviromentView.class.getName()).log(Level.SEVERE, null, ex);
            }

            char combatOptions = talkToNPC.trim().toUpperCase().charAt(0);

            switch (combatOptions) {

                case 'A':
                    attack(hero, monster);
                    break;

                case 'D':
                    defend(hero, monster);
                    break;

                case 'I':
                    item();
                    break;

                case 'F':
                    return false;

                default:
                    this.console.println("Invalid Option");

            }

            if (combatAI(monster)) {
                attack(monster, hero);
            } else {
                defend(monster, hero);
            }

 

        }
        return false;
    }

    private void attack(Actor attacker, Actor defender) {
        long damage = BattleScene.calcDamage(BattleScene.totalAttack(attacker), BattleScene.totalDefense(defender));
        BattleScene.damageTaken(defender, damage);
        this.console.println(attacker.getActorName() + " attacks " + defender.getActorName() + " for " + damage + " damage.");
    }

    private void defend(Actor defender, Actor attacker) {
        long damage = BattleScene.defense(attacker, BattleScene.totalAttack(defender));
        //BattleScene.damageTaken(defender, damage);
        this.console.println(defender.getActorName() + " defends, It seems pointless.");
    }

    private void item() {

    }

    private boolean combatAI(Actor monster) {

        Random combatAI = new Random();

        int n = combatAI.nextInt(3) + 1;

        if (n <= 2) {
            return true;
        }
        return false;
    }

    private void itemRequiredScene() {

        Location currentLocation
                = EscapeIsland.getCurrentGame().getMap().getLocations()[Actor.Hero.getActorcoordinates().x][Actor.Hero.getActorcoordinates().y];

        if (Actor.Hero.getCurrentItem() != currentLocation.getItemRequired()) {
            this.console.println("\n***********************************************************"
                    + "\n***********************************************************"
                    + "\n*                                                         *"
                    + "\n*The " + currentLocation.getItemRequired().getItemName() + "                   *"
                    + "\n*is required to progress                                  *"
                    + "\n                                                          *"
                    + "\n***********************************************************"
                    + "\n***********************************************************");
            return;
        } else {
            this.console.println("\n***********************************************************"
                    + "\n***********************************************************"
                    + "\n*                                                         *"
                    + "\n*The " + currentLocation.getItemRequired().getItemName() + "                   *"
                    + "\n* allowed you to progress.                                *"
                    + "\n*                                                         *"
                    + "\n***********************************************************"
                    + "\n***********************************************************");
        }
    }

    private void getLoot() {

        Location currentLocation
                = EscapeIsland.getCurrentGame().getMap().getLocations()[Actor.Hero.getActorcoordinates().x][Actor.Hero.getActorcoordinates().y];

        Actor player1 = EscapeIsland.getCurrentPlayer().getActor();
        try {
            InventoryControl.addItemToInventory(player1, currentLocation.getObtainItem());
        } catch (InventoryControlException ex) {
            this.console.println(ex.getMessage());
        }

        this.console.println("\n***********************************************************"
                + "\n***********************************************************"
                + "\n*                                                         *"
                + "\n*Congradulations you have obtained " + currentLocation.getObtainItem().getItemName() + "        *"
                + "\n*                                                         *"
                + "\n***********************************************************"
                + "\n***********************************************************");
        return;
    }

    private boolean talkToNPC() {
        boolean javaIsDumb = true;
        while (javaIsDumb) {
            this.console.println("A - Hello");
            this.console.println("B - Who are you?");
            this.console.println("C - What things roam?");
            this.console.println("D - Good bye");

            
            String talkToNPC = null;
            try {
                talkToNPC = this.keyboard.readLine();
            } catch (IOException ex) {
                Logger.getLogger(InteractWithEnviromentView.class.getName()).log(Level.SEVERE, null, ex);
            }

            char chatOptions = talkToNPC.trim().toUpperCase().charAt(0);

            switch (chatOptions) {

                case 'A':
                    optionAChat();
                    break;

                case 'B':
                    optionBChat();
                    break;

                case 'C':
                    optionCChat();
                    break;

                case 'D':
                    optionDChat();
                    return true;

                case 'Q':
                    return true;

                default:
                    this.console.println("Invalid Option");

            }
        }
        return false;
    }

    private void puzzle() {
        this.console.println("\n***********************************************************"
                + "\n***********************************************************"
                + "\n*                                                         *"
                + "\n*                                                         *"
                + "\n*Puzzle Text                                              *"
                + "\n*                                                         *"
                + "\n*                                                         *"
                + "\n*                                                         *"
                + "\n***********************************************************"
                + "\n***********************************************************");
        return;
    }

    private void riddle(Riddle riddle) {
        this.console.println("\n***********************************************************"
                + "\n***********************************************************"
                + "\n*                                                         *"
                + "\n*                                                         *"
                + riddle
                + "\n*                                                         *"
                + "\n*                                                         *"
                + "\n***********************************************************"
                + "\n***********************************************************");

        String rScanner = null;
        try {
            rScanner = this.keyboard.readLine();
        } catch (IOException ex) {
            Logger.getLogger(InteractWithEnviromentView.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (rScanner == riddle.getAnswer()) {
            this.console.println("Correct!");
            riddle.setRiddleDone(true);
        } else {
            this.console.println("Incorrect, try agian.");
        }
        return;
    }

    private void optionAChat() {
        this.console.println("Hello");
        this.console.println("Marcus says \"Hey their sunny boy, welcome to our own personal hell. "
                + "Before you explore the island you should equip yourself. Strange things travel in the night"
                + " I'm to old to be of much use now, but I have found some "
                + "interesting trinkets in my glory days. Loot this camp and the camp directly to the "
                + "west before you explore the island... or dont, its your life. \"");

    }

    private void optionBChat() {
        this.console.println("Who are you?");
        this.console.println("Marcus says \"My name is Marcus Stone, I was an explorer in the Queens Royal Navy, "
                + "my vessal marroned here, and I've been surviving ever since. Its been over 30 years now. \"");

    }

    private void optionCChat() {
        this.console.println("What things roam?");
        this.console.println("Marcus says \" Monsters, and the remenants of my old crew. There is something odd about this place. "
                + "Something that makes the blood curl and myths become real. Best ye watch yourself.\"");
    }

    private void optionDChat() {
        this.console.println("Good bye");
        this.console.println("Marcus says \"God speed and good luck... you'll need it.\"");

    }

    private boolean deathScene(Actor hero, Actor monster) {

        if (BattleScene.death(hero)) {
            this.console.println("YOU HAVE DIED");
            hero.setActorCoordinates(new Point(7, 2));
            hero.setActorHitPoints(15);
            return false;
            
        } else if (BattleScene.death(monster)) {
            this.console.println("VICTORY!");
            return false;
        }
        return true;

    }

}
