import io.reactivex.rxjava3.internal.operators.single.SingleJust;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;
import io.reactivex.rxjava3.core.Maybe;

public class JavaMonad {
    public static void main(String[] args) {
        System.out.println("===== Null =====");
        FamilyTree familyTree = new FamilyTree();
        familyTree.run();

        System.out.println("\n===== Try =====");
        Divide divide = new Divide();
        divide.run();
    }
}

class FamilyTree {
    @Getter
    @AllArgsConstructor
    class Person {
        private String relation;
        private Person father;
        private Person mother;
    }

    Person createFamilyTree() {
        return new Person("Me",
                new Person("Father",
                        new Person("GrandFather",
                                new Person(null,                null, null),
                                new Person("Great-GrandMother", null, null)),
                        new Person("GrandMother", null, null)),
                new Person("Mother",
                        new Person("Maternal GrandFather", null, null),
                        new Person("Maternal GrandMother", null, null)));
    }

    Person person;
    FamilyTree() {
        this.person = createFamilyTree();
    }

    void find1() {
        if ( person.getFather() != null ) {
            Person father = person.getFather();
            if ( father.getFather() != null ) {
                Person grandFather = father.getFather();
                if ( grandFather.getFather() != null ) {
                    Person greatGrandFather = grandFather.getFather();
                    if ( greatGrandFather.getRelation() != null )
                        System.out.println( "1-1: " + greatGrandFather.getRelation() );
                    else
                        System.out.println( "1-1: Failed" );
                } else {
                    System.out.println( "1-1: Failed" );
                }
            }  else {
                System.out.println( "1-1: Failed" );
            }
        } else {
            System.out.println( "1-1: Failed" );
        }

        if ( person.getMother() != null ) {
            Person mother = person.getMother();
            if ( mother.getMother() != null ) {
                Person maternalGrandMother = mother.getMother();
                if ( maternalGrandMother.getRelation() != null )
                    System.out.println( "1-2: " + maternalGrandMother.getRelation() );
                else
                    System.out.println( "1-2: Failed" );
            } else {
                System.out.println( "1-2: Failed" );
            }
        } else {
            System.out.println( "1-1: Failed" );
        }
    }

    void find2() {
        Optional.of( person )
                .map( Person::getFather   )
                .map( Person::getFather   )
                .map( Person::getFather   )
                .map( Person::getRelation )
                .ifPresentOrElse(
                        info -> System.out.println( "2-1: " + info ),
                        ()   -> System.out.println( "2-1: Failed"  )
                );

        Optional.of( person )
                .map( Person::getMother   )
                .map( Person::getMother   )
                .map( Person::getRelation )
                .ifPresentOrElse(
                        info -> System.out.println( "2-2: " + info ),
                        ()   -> System.out.println( "2-2: Failed"  )
                );
    }

    void find3() {
        Maybe.just( person )
                .map( Person::getFather   )
                .map( Person::getFather   )
                .map( Person::getFather   )
                .map( Person::getRelation )
                .subscribe(
                        info -> System.out.println( "3-1: " + info ),
                        e    -> System.out.println( "3-1: Failed"  )
                );

        Maybe.just(person)
                .map( Person::getMother   )
                .map( Person::getMother   )
                .map( Person::getRelation )
                .subscribe(
                        info -> System.out.println( "3-2: " + info ),
                        e    -> System.out.println( "3-2: Failed"  )
                );
    }

    void find4() {
        String greatGrandFather =
        Option.of( person )
                .map( Person::getFather   )
                .map( Person::getFather   )
                .map( Person::getFather   )
                .map( Person::getRelation )
                .getOrElse( "Failed" );
        System.out.println( "4-1: " + greatGrandFather );

        String maternalGrandMotherName =
            Option.of( person )
                .map( Person::getMother   )
                .map( Person::getMother   )
                .map( Person::getRelation )
                .getOrElse( "Failed" );
        System.out.println( "4-2: " + maternalGrandMotherName );
    }

    void run() {
        find1(); // If Else
        find2(); // Java Optional
        find3(); // RxJava Maybe
        find4(); // Vavr Option
    }
}


class Divide {
    int num = 100;

    void divide1( int divide_num ) {
        try {
            System.out.println( "1: " + ( num / divide_num ) );
        } catch ( ArithmeticException e ) {
            System.out.println( "1: Can't divide by 0" );
        }
    }

    void divide2( int divide_num ) {
        SingleJust.just( num )
                .map( num -> num / divide_num )
                .subscribe(
                        result -> System.out.println( "2: " + result         ),
                        e      -> System.out.println( "2: Can't divide by 0" )
                );
    }

    void divide3( int divide_num ) {
        Try.of( () -> num / divide_num )
                .onSuccess( result -> System.out.println( "3: " + result         ) )
                .onFailure( e      -> System.out.println( "3: Can't divide by 0" ) );
    }

    void run() {
        divide1( 0  );
        divide1( 10 );
        divide2( 0  );
        divide2( 10 );
        divide3( 0  );
        divide3( 10 );
    }
}