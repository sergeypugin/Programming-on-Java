package WorldModel.Animals.Fox;

import WorldModel.Animal;
import WorldModel.Exceptions.UncheckedException;
import WorldModel.PhysicalObject;
import WorldModel.Interfaces.Predator;

public final class Fox extends Animal implements Predator {
    public Fox(String name) {
        super(name);
    }

    public void slip() throws UncheckedException {
        this.swim(0.0001);
        System.out.printf("%s наскочил(-а) на что-то мягкое, скользкое, мокрое. %s не удержался и упал\n", name, name);
    }

    public void slip(PhysicalObject obj) throws UncheckedException {
        this.swim(0.0001);
        System.out.printf("%s наскочил(-а) на %s. %s не удержался и упал\n", name, obj.getName(), name);
    }

    public void growl(String obj) {
        System.out.printf("%s огрызнулся(-ась): \"Тьфу, да это опять ты, %s!\"\n", name, obj);
    }

    public void mock() {
        System.out.printf("%s сел на задние лапы, задрал морду и начал выть, скулить, скрипеть зубами, щелкать языком...\n", name);
    }

    @Override
    public void hunt(PhysicalObject place) {
        System.out.printf("%s опытным взглядом бывалого охотника оглядел %s.\n", name, place.getName());
        if (place.getName().toLowerCase().contains("скала")) {
            System.out.printf("\"Не стоит и пробовать, только ноги переломаешь!\" - подумал %s. - Зато поразвлечь их можно.\n", name);
        } else {
            System.out.printf("%s побежал дальше за жертвами.\n", name);
        }
    }
}