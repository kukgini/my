import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observables.StringObservable;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class RXTest {

    /**
     * StringObservable 이 InputStream 을 byteArray 로 변환하는 과정을 테스트.
     *
     * map 과 flatMap 의 차이를 알게됨.
     *
     * 동일 observable 에서 자료형 변환에는 map 을,
     * 하나의 observable 에서 다른 obvervable 을 생성하려면 flatMap 을 사용한다.
     *
     * @param args
     */
    public static void main(String[] args) {

        byte[] input = (new String("ABCDEFGHIJKL")).getBytes();
        InputStream in = new ByteArrayInputStream(input);

        StringObservable.from(in, 3)

//        .map(new Func1<byte[], ByteBuf>() {
//            @Override
//            public ByteBuf call(byte[] bytes) {
//                System.out.println(bytes);
//                return Unpooled.wrappedBuffer(bytes);
//            }})

        .subscribe(new Action1<byte[]>() {
            @Override
            public void call(byte[] bytes) {
                System.out.println(new String(bytes));
            }
        });
    }


}
