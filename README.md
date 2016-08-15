# SwipeCardsView
SwipeCardsView
### 来由
之所以做这个效果是因为项目中有这个效果需要实现。

 - 一开始我有在github上找到不少类似的库，但是发现放在项目中会发现要么有锯齿，要么就是卡顿，总之就是效果不好，其实绝大多数的库都和[Swipecards](https://github.com/Diolor/Swipecards)差不多，做法是重写了adapterview，然后设置监听，在监听里做移动和缩放。移动用的是设置view的x和y坐标，这样做法的弊端是会频繁触发view树重绘，效率不高。
 - 后来发现这个库[android-card-slide-panel](https://github.com/xmuSistone/android-card-slide-panel)，它的做法是重写了viewgroup，里面view的数目是固定的，卡片的滑动是通过viewDragHelper来做的，没有锯齿同时也不卡顿了，但是viewDragHelper有问题：<br>
 1、在多个手指同时滑动的时候会有概率出现pointIndex out of range异常，这个问题倒没什么，我通过修改viewDragHelper的源码已经解决了这个问题；<br>
 2、当用picasso或者glide加载图片以后，在手指拖动卡片的过程中有时会莫名的收到MotionEvent的UP事件，导致卡片回到了初始位置，这个问题折腾了我半天，后来的解决办法是弃用了viewDragHelper，直接使用Scroller。<br>
 3、还有一点要吐槽下，这个库的使用太麻烦了，耦合太重，集成到项目里比较费事。<br>
### 效果图
<td>
	 <img src="gif/pic3.gif" width="290" height="485" />
	 <img src="gif/pic1.gif" width="290" height="485" />
	 <img src="gif/pic2.gif" width="290" height="485" />
</td>
###特点

 1. 如丝般顺滑，这是公司产品体验过后的评价；
 2. 灵活，可以通过设置几个属性，很容易就能定制可视卡片的数量和卡片的叠加垂直偏移量、缩放比例，透明度比例；
 3. 使用方便，直接setadapter就可以使用了，数据更新调用swipeCardsView.notifyDatasetChanged(index);就行了，下面有使用说明。

###Gradle

```groovy
dependencies {
   compile 'com.huxq17.android:SwipeCardsView:1.3.0'
   //依赖下面的库
   compile 'com.android.support:appcompat-v7:23.0.1'
}
```
###Example
####xml：
```xml
<android.support.design.widget.CoordinatorLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:card="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fitsSystemWindows="true">

    <com.huxq17.swipecardsview.SwipeCardsView
        android:id="@+id/swipCardsView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="#f3f3f3"
        card:alphaOffsetStep="40"
        card:scaleOffsetStep="0.08"
        card:yOffsetStep="20dp" />

    ...省略部分代码...

</android.support.design.widget.CoordinatorLayout>
```
对card中属性的解释：
```xml
<declare-styleable name="SwipCardsView">
        <!-- yOffsetStep定义的是卡片之间在y轴方向上的偏移量，单位是dp，
        举个例子，可见的卡片有3个，如果步长是20dp，从前往后看，卡片y轴坐标会依次增加20dp，表现上就是后面一张卡片底部有20dp会露出来
        如果值是负的，如 -20dp，那么表现则相反。
         如果不需要对卡片进行y轴方向上的偏移量处理，不设置这个属性或者设置为0dp就可以了-->
        <attr name="yOffsetStep" format="dimension" />
        <!-- alpha定义的取值范围是0-100，所以alpha的步长也得在这个范围之内，
        举个例子，可见的卡片有3个，如果步长是40，那么最前面的alpha是100，后面一点的是60，最后面的是20
         如果不需要对卡片进行透明度处理，不设置这个属性或者设置为0就可以了-->
        <attr name="alphaOffsetStep" format="integer" />
        <!-- scale定义的取值范围是0-1，所以scale的步长也得在这个范围之内，
        举个例子，可见的卡片有3个，如果步长是0.08，那么最前面的alpha是1，后面一点的是0.92，最后面的是0.84
        值得注意的是 x 和 y同时被缩放了(1 - scaleStep*index)
        如果不需要对卡片进行缩放处理，不设置这个属性或者设置为0就可以了-->
        <attr name="scaleOffsetStep" format="float" />
    </declare-styleable>
```
####adapter：
1、抽象类
```java
public abstract class BaseCardAdapter<T> {
   /**
        * 获取卡片的数量
        *
        * @return
       */
       public abstract int getCount();

    /**
     * 获取卡片view的layout id
     *
     * @return
     */
    public abstract int getCardLayoutId();

    /**
     * 将卡片和数据绑定在一起
     *
     * @param position 数据在数据集中的位置
     * @param cardview 要绑定数据的卡片
     */
    public abstract void onBindData(int position, View cardview);

    /**
     * 获取可见的cardview的数目，默认是3
     * @return
     */
    public int getVisibleCardCount() {
        return 3;
    }
}
```
2、实现
```java
public class MeiziAdapter extends BaseCardAdapter {
    private List<ContentBean> datas;
    private Context context;

    public MeiziAdapter(List<ContentBean> datas, Context context) {
        this.datas = datas;
        this.context = context;
    }

   @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public int getCardLayoutId() {
        return R.layout.card_item;
    }

    @Override
    public void onBindData(int position, View cardview) {
        if (datas == null || datas.size() == 0) {
            return;
        }
        ImageView imageView = (ImageView) cardview.findViewById(R.id.iv_meizi);
        ContentBean meizi = datas.get(position);
        String url = meizi.getUrl();
        Picasso.with(context).load(url).config(Bitmap.Config.RGB_565).into(imageView);
    }

    /**
     * 如果可见的卡片数是3，则可以不用实现这个方法
     * @return
     */
    @Override
    public int getVisibleCardCount() {
        return super.getVisibleCardCount();
    }
}

```


####activity or fragment：
```java
    /**
     * 卡片向左边飞出
     */
    public void doLeftOut() {
        swipeCardsView.slideCardOut(SwipeCardsView.SlideType.LEFT);
    }

    /**
     * 卡片向右边飞出
     */
    public void doRightOut() {
        swipeCardsView.slideCardOut(SwipeCardsView.SlideType.RIGHT);
    }
    /**
     * 从头开始，重新浏览
     */
    public void doRetry() {
        //必须先改变adapter中的数据，然后才能由数据变化带动页面刷新
        if (mList != null) {
            adapter.setData(mList);
            swipeCardsView.notifyDatasetChanged(0);
        }
    }
    /**
     * 显示cardsview
     */
    private void show() {
        if (adapter == null) {
            adapter = new MeiziAdapter(mList, getActivity());
            swipeCardsView.setAdapter(adapter);
        } else {
           //if you want to change the UI of SwipeCardsView,you must modify the data first
           adapter.setData(mList);
           swipeCardsView.notifyDatasetChanged(curIndex);
        }
    }

	  ...省略部分代码...
        swipeCardsView = (SwipeCardsView) container.findViewById(R.id.swipCardsView);
	   //设置滑动监听
        swipeCardsView.setCardsSlideListener(new SwipeCardsView.CardsSlideListener() {
            @Override
            public void onShow(int index) {
                LogUtils.i("test showing index = "+index);
            }

            @Override
            public void onCardVanish(int index, SwipeCardsView.SlideType type) {
                String orientation = "";
                switch (type){
                    case LEFT:
                        orientation="向左飞出";
                        break;
                    case RIGHT:
                        orientation="向右飞出";
                        break;
                }
            }

            @Override
            public void onItemClick(View cardImageView, int index) {
                toast("点击了 position="+index);
            }
        });
```

### 更新日志：<br/>
    2016-8-15：
    1.Fix #9 and you can call retainLastCard method to retain the last card.
    2.SwipeCardsView will not call onShow method when has no card showing.

###PS:
    所用的数据是从别的网站上爬下来的，所以网站数据结构变化会导致demo崩掉。因为这只是个demo我就没有做特殊的处理，
    崩掉以后如果发现了我会及时改过来，如果app崩掉或者没有数据的话，建议直接看使用说明，不一定要把demo跑起来。

## License

    Copyright (C) 2016 huxq17

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

