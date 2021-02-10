/*
 * Copyright (c) 2018, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package no.nordicsemi.android.nrfmesh.node.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import no.nordicsemi.android.mesh.transport.Element;
import no.nordicsemi.android.mesh.transport.ProvisionedMeshNode;
import no.nordicsemi.android.mesh.utils.CompanyIdentifiers;
import no.nordicsemi.android.mesh.utils.MeshAddress;
import no.nordicsemi.android.mesh.utils.MeshParserUtils;
import no.nordicsemi.android.nrfmesh.ListViewAdapter;
import no.nordicsemi.android.nrfmesh.R;
import no.nordicsemi.android.nrfmesh.TextAddress;
import no.nordicsemi.android.nrfmesh.TextGroup;
import no.nordicsemi.android.nrfmesh.TextString;
import no.nordicsemi.android.nrfmesh.widgets.RemovableViewHolder;

public class NodeAdapter extends RecyclerView.Adapter<NodeAdapter.ViewHolder> {
    private final List<ProvisionedMeshNode> mNodes = new ArrayList<>();

    private OnItemClickListener mOnItemClickListener;
    public static FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static DatabaseReference myRef = database.getReference("data_text");
    final public static DatabaseReference myRef2 = myRef.child("data01");
    final public static DatabaseReference myRef3 = myRef.child("data02");

    public NodeAdapter(@NonNull final LifecycleOwner owner,
                       @NonNull final LiveData<List<ProvisionedMeshNode>> provisionedNodesLiveData) {
        provisionedNodesLiveData.observe(owner, nodes -> {
            if (nodes != null) {
                mNodes.clear();
                mNodes.addAll(nodes);
                notifyDataSetChanged();
            }
        });
    }

    public void setOnItemClickListener(@NonNull final OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.network_item, parent, false);
        return new NodeAdapter.ViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final ProvisionedMeshNode node = mNodes.get(position);
        if (node != null) {
            String nodename = node.getNodeName();
            myRef2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot ds: snapshot.getChildren()){
                        TextString model = ds.getValue(TextString.class);
                        if(model.getZ01_roomnumber().toString().toLowerCase().equals(nodename.toLowerCase())){
                            String name = model.getZ02_name();
                            String gender = model.getZ03_gender();
                            String type = model.getZ04_type();
                            String uid = model.getZ05_uid();
                            String date = model.getZ06_date();
                            String order1 = model.getZ07_order1();
                            String order2 = model.getZ08_order2();
                            String order3 = model.getZ09_order3();
                            String main = model.getZ10_main();
                            String resident = model.getZ11_resident();
                            String nurse = model.getZ12_nurse();
                            holder.name.setText(node.getNodeName());
                            holder.mname.setText(name);
                            holder.unicastAddress.setText(MeshParserUtils.bytesToHex(MeshAddress.addressIntToBytes(node.getUnicastAddress()), false));

                            String address = holder.unicastAddress.getText().toString();
                            myRef2.child(model.getZ01_roomnumber()).setValue(new TextString(nodename, name, gender, type, uid, date, order1,
                                    order2, order3, main, resident, nurse, address));

                            final Map<Integer, Element> elements = node.getElements();
                            if (!elements.isEmpty()) {
                                holder.nodeInfoContainer.setVisibility(View.VISIBLE);
                                if (node.getCompanyIdentifier() != null) {
                                    holder.companyIdentifier.setText(CompanyIdentifiers.getCompanyName(node.getCompanyIdentifier().shortValue()));
                                } else {
                                    holder.companyIdentifier.setText(R.string.unknown);
                                }
                                holder.elements.setText(String.valueOf(elements.size()));
                                holder.models.setText(String.valueOf(getModels(elements)));
                            } else {
                                holder.companyIdentifier.setText(R.string.unknown);
                                holder.elements.setText(String.valueOf(node.getNumberOfElements()));
                                holder.models.setText(R.string.unknown);
                            }

                /*holder.mroomnumber.setText(roomnumber);
                holder.mgender.setText(gender);
                holder.mtype.setText(type);
                holder.muid.setText(uid);
                holder.mdate.setText(date);
                holder.morder1.setText(order1);
                holder.morder2.setText(order2);
                holder.morder3.setText(order3);
                holder.mmain.setText(main);
                holder.mresident.setText(resident);
                holder.mnurse.setText(nurse);*/
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mNodes.size();
    }

    public ProvisionedMeshNode getItem(final int position) {
        if (mNodes.size() > 0 && position > -1) {
            return mNodes.get(position);
        }
        return null;
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    private int getModels(final Map<Integer, Element> elements) {
        int models = 0;
        for (Element element : elements.values()) {
            models += element.getMeshModels().size();
        }
        return models;
    }

    @FunctionalInterface
    public interface OnItemClickListener {
        void onConfigureClicked(final ProvisionedMeshNode node);
    }

    final class ViewHolder extends RemovableViewHolder {
        @BindView(R.id.container)
        FrameLayout container;
        @BindView(R.id.node_name)
        TextView name;
        @BindView(R.id.configured_node_info_container)
        View nodeInfoContainer;
        @BindView(R.id.unicast)
        TextView unicastAddress;
        @BindView(R.id.company_identifier)
        TextView companyIdentifier;
        @BindView(R.id.elements)
        TextView elements;
        @BindView(R.id.models)
        TextView models;

        @BindView(R.id.name)
        TextView mname;

        /*mroomnumber = itemView.findViewById(R.id.roomnumber);

        mgender = itemView.findViewById(R.id.gender);
        mtype = itemView.findViewById(R.id.type);
        muid = itemView.findViewById(R.id.uid);
        mdate = itemView.findViewById(R.id.date);
        morder1= itemView.findViewById(R.id.order1);
        morder2= itemView.findViewById(R.id.order2);
        morder3= itemView.findViewById(R.id.order3);
        mmain= itemView.findViewById(R.id.main);
        mresident= itemView.findViewById(R.id.resident);
        mnurse= itemView.findViewById(R.id.nurse);*/

        private ViewHolder(final View provisionedView) {
            super(provisionedView);
            ButterKnife.bind(this, provisionedView);
            container.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onConfigureClicked(mNodes.get(getAdapterPosition()));
                }
            });
        }
    }

}
