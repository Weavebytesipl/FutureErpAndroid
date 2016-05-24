/*

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

  Under research & development by Weavebytes, weavebytes@gmail.com
 */

package weavebytes.com.futureerp.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import weavebytes.com.futureerp.R;
/*
class for customize adapter
 */
public class MyAdapter extends ArrayAdapter{

    private final Activity context;
    public final  ArrayList<String> list ;
    private final Integer[] ImgId;

    public MyAdapter(Activity context, ArrayList list, Integer[] ImgId) {
        super(context, R.layout.mylist, list);
        this.context = context;
        this.list = list;
        this.ImgId = ImgId;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.mylist, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
        ImageView imageView = (ImageView)rowView.findViewById(R.id.imageView);

        txtTitle.setText(list.get(position));
        imageView.setImageResource(ImgId[position]);
        return rowView;
    }
}//MyAdapter

